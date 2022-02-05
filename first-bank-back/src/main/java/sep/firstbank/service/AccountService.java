package sep.firstbank.service;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sep.firstbank.clients.PccClient;
import sep.firstbank.dtos.*;
import sep.firstbank.exceptions.*;
import sep.firstbank.model.Account;
import sep.firstbank.model.CreditCard;
import sep.firstbank.model.Invoice;
import sep.firstbank.model.Transaction;
import sep.firstbank.repositories.AccountRepository;
import sep.firstbank.repositories.InvoiceRepository;
import sep.firstbank.repositories.TransactionRepository;
import sep.firstbank.util.CreditCardValidator;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import javax.security.auth.login.AccountNotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CreditCardService cardService;
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeService exchangeService;
    //private final SensitiveDataConverter sensitiveDataConverter;
    private final PccClient pccClient;
    private static final long MIG = 603759;
    private static final String BANK_NUMBER = "00000";

    @Value("${pcc.url-transfer}")
    private String pccUrl;


    @Autowired
    public AccountService(AccountRepository accountRepository, CreditCardService cardService, InvoiceRepository invoiceRepository, TransactionRepository transactionRepository, ExchangeService exchangeService, PccClient pccClient){
        this.accountRepository = accountRepository;
        this.cardService = cardService;
        this.invoiceRepository = invoiceRepository;
        this.transactionRepository = transactionRepository;
        this.exchangeService = exchangeService;
        this.pccClient = pccClient;
    }

    public Account getById(long id) throws AccountNotFoundException {
        try {
            if(accountRepository.findById(id).isPresent()) return accountRepository.findById(id).get();
            throw new AccountNotFoundException();
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new AccountNotFoundException();
    }

    public void validate(MerchantCredentialsDTO dto) throws AccountNotFoundException {
        Account account = accountRepository.findByMerchantIdAndMerchantPassword(dto.getMid(), dto.getMpassword());
        if (account == null) throw new AccountNotFoundException();
    }

    public MerchantDTO register(CardInfoDTO dto) throws CreditCardNotFoundException, CreditCardInfoNotValidException, AccountNotFoundException {
        //String pan = sensitiveDataConverter.convertToDatabaseColumn(dto.getPan());
        CreditCard card = cardService.getByPAN(dto.getPan());
        CreditCardValidator.validate(card, dto);
        Account a = getById(card.getAccountId());
        a.setMerchantId(a.getId() + MIG);
        a.setMerchantPassword(RandomStringUtils.randomAlphanumeric(20));
        accountRepository.save(a);
        log.info("Account (id=" + a.getId() + ") registered for e-banking services");
        return new MerchantDTO(a.getMerchantId(), a.getMerchantPassword());
    }

    public Invoice getInvoice(long invoiceId) throws InvoiceNotFoundException {
        if (invoiceRepository.findById(invoiceId).isEmpty()) {
            throw new InvoiceNotFoundException();
        }
        return invoiceRepository.findById(invoiceId).get();
    }

    public Invoice pay(CardInfoDTO dto, Invoice invoice) throws InvoiceAlreadyPaidException, NoMoneyException, CreditCardNotFoundException, CreditCardInfoNotValidException, CurrencyUnsupportedException, ExternalTransferException {
        if (invoice.getTransaction() != null) {
            log.warn("Attempt to pay invoice (id=" + invoice.getId() + ") that was already payed for");
            throw new InvoiceAlreadyPaidException();
        }
        if(isCardInThisBank(dto.getPan())) {
            return payInThisBank(dto, invoice);
        }
        return callPCC(dto, invoice);
    }

    private Invoice payInThisBank(CardInfoDTO dto, Invoice invoice) throws CreditCardNotFoundException, CreditCardInfoNotValidException, NoMoneyException, CurrencyUnsupportedException {
        CreditCard card = cardService.getByPAN(dto.getPan());
        CreditCardValidator.validate(card, dto);
        return makeTransaction(card, invoice);
    }

    private Invoice makeTransaction(CreditCard card, Invoice invoice) throws NoMoneyException, CurrencyUnsupportedException {
        Account buyer = accountRepository.getById(card.getAccountId());
        if(!exchangeService.conversionSupported(buyer.getCurrency(), invoice.getCurrency())) {
            log.info("Invalid currency conversion attempt (" + buyer.getCurrency() + " to " + invoice.getCurrency() + ')');
            throw new CurrencyUnsupportedException("Your account's currency is not supported");
        }
        Account seller = accountRepository.getById(invoice.getAccountId());
        BigDecimal buyerDiff = exchangeService.exchange(invoice.getCurrency(), invoice.getAmount(), buyer.getCurrency());
        BigDecimal sellerDiff = exchangeService.exchange(invoice.getCurrency(), invoice.getAmount(), seller.getCurrency());
        if (buyer.getBalance().compareTo(buyerDiff) > 0) {
            Transaction transaction = transactionRepository.save(new Transaction(invoice, buyer.getId(), seller.getId()));
            invoice.setTransaction(transaction);
            invoiceRepository.save(invoice);
            buyer.setBalance(buyer.getBalance().subtract(buyerDiff));
            seller.setBalance(seller.getBalance().add(sellerDiff));
            accountRepository.save(buyer);
            accountRepository.save(seller);
            log.info("Transaction (id=" + transaction.getId() + ") created for invoice (id=" + invoice.getId() +
                    ") - seller account id=" + seller.getId() + " buyer account id=" + buyer.getId());
            log.info("Account (id=" + buyer.getId() + ") new balance is " + buyer.getBalance());
            log.info("Account (id=" + seller.getId() + ") new balance is " + seller.getBalance());
            return invoice;
        } else {
            log.info("Unsuccessful transaction (insufficient funds) for invoice (id=" + invoice.getId() +
                    ") - seller account id=" + seller.getId() + " buyer account id=" + buyer.getId());
            throw new NoMoneyException();
        }
    }

    private Invoice callPCC(CardInfoDTO dto, Invoice invoice) throws ExternalTransferException {
        //TODO : make pcc client and second bank
        Account seller = accountRepository.getById(invoice.getAccountId());
        BigDecimal amountToMove = exchangeService.exchange(invoice.getCurrency(), invoice.getAmount(), seller.getCurrency());

        PCCRequestDTO request = new PCCRequestDTO(invoice.getId(), LocalDateTime.now(), dto.getPan(), dto.getCardHolderName(), dto.getExpirationDate(), dto.getSecurityCode(), amountToMove, seller.getCurrency().toString(), seller.getId());
        PCCResponseDTO response = pccClient.bankPaymentResponse(URI.create(pccUrl), request);
        if (response.getStatus().equals("SUCCESS")) {
            Transaction transaction = transactionRepository.save(new Transaction(invoice, response.getFromId(), seller.getId()));
            invoice.setTransaction(transaction);
            invoiceRepository.save(invoice);
            seller.setBalance(seller.getBalance().add(amountToMove));
            accountRepository.save(seller);
            return invoice;
        } else {
            log.warn("Attempt to pay invoice (id=" + invoice.getId() + ") was denied by external bank");
            throw new ExternalTransferException();
        }
    }

    public PCCResponseDTO receiveRequestFromPcc (PCCRequestDTO request){
        PCCResponseDTO response;
        CreditCard card;
        try {
            card = cardService.getByPAN(request.getPanNumber());
            if(!isCardInThisBank(card.getPAN()))
                return new PCCResponseDTO("CARD_NOT_IN_THIS_BANK", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), LocalDateTime.now(),  -1);
        } catch (CreditCardNotFoundException e) {
            return new PCCResponseDTO("NO_CREDIT_CARD", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), LocalDateTime.now(),  -1);
        }
        Account buyer = accountRepository.getById(card.getAccountId());

        if (buyer.getBalance().compareTo(request.getAmount()) > 0) {
            Transaction transaction = transactionRepository.save(new Transaction( buyer.getId(), request.getToId(), request.getAmount(), request.getCurrency(), LocalDateTime.now()));

            buyer.setBalance(buyer.getBalance().subtract(request.getAmount()));
            accountRepository.save(buyer);
            response = new PCCResponseDTO("SUCCESS", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), transaction.getCreated(), buyer.getId());
        } else {
            response = new PCCResponseDTO("NO_MONEY", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), LocalDateTime.now(), buyer.getId());
            log.warn("Attempt to pay invoice was denied because of insufficient funds (buyer id=" + buyer.getId() + ")");
        }
        return response;
    }

    private boolean isCardInThisBank(String pan){
        String cardBankNumber = pan.substring(0, 5);
        return cardBankNumber.equals(BANK_NUMBER);
    }

    public Invoice payWithQR(MultipartFile qrCode, Invoice invoice) throws InvoiceAlreadyPaidException, NoMoneyException, CreditCardNotFoundException, CreditCardInfoNotValidException, CurrencyUnsupportedException, ExternalTransferException, IOException {
        if (invoice.getTransaction() != null) {
            log.warn("Attempt to pay invoice (id=" + invoice.getId() + ") that was already payed for");
            throw new InvoiceAlreadyPaidException();
        }
        String decodedResult = getDecodedString(qrCode);
        CardInfoDTO cardInfo = createCardInfo(decodedResult);

        if(isCardInThisBank(cardInfo.getPan())) {
            return payInThisBank(cardInfo, invoice);
        }
        return callPCC(cardInfo, invoice);
    }

    private CardInfoDTO createCardInfo(String decodedResult){
        String[] tokens = decodedResult.split(";");
        return new CardInfoDTO(tokens[0], tokens[1], tokens[2], tokens[3]);
    }

    private String getDecodedString(MultipartFile qrCode) throws IOException {
        String path = new File("src/main/resources/qrs").getAbsolutePath();
        Path filepath = Paths.get(path, qrCode.getOriginalFilename());

        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(qrCode.getBytes());
        } catch (IOException e2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e2.getMessage());
        }
        return decodeQrCode(filepath);
    }

    public static String decodeQrCode(Path filePath) throws IOException {
        File img = new File(filePath.toString());
        BufferedImage bufferedImage = ImageIO.read(img);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }

}
