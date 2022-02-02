package sep.firstbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CreditCardService cardService;
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeService exchangeService;
    private final PccClient pccClient;
    private static final long MIG = 603759;
    private static final String BANK_NUMBER = "00000";

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
        if(accountRepository.findById(id).isPresent()) return accountRepository.findById(id).get();
        throw new AccountNotFoundException();
    }

    public void validate(MerchantCredentialsDTO dto) throws AccountNotFoundException {
        Account account = accountRepository.findByMerchantIdAndMerchantPassword(dto.getMid(), dto.getMpassword());
        if (account == null) throw new AccountNotFoundException();
    }

    public MerchantDTO register(CardInfoDTO dto) throws CreditCardNotFoundException, CreditCardInfoNotValidException, AccountNotFoundException {
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
        if (!invoiceRepository.findById(invoiceId).isPresent()) {
            throw new InvoiceNotFoundException();
        }
        return invoiceRepository.findById(invoiceId).get();
    }

    public Invoice pay(CardInfoDTO dto, Invoice invoice) throws InvoiceAlreadyPaidException, NoMoneyException, CreditCardNotFoundException, CreditCardInfoNotValidException, CurrencyUnsupportedException {
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

    private Invoice callPCC(CardInfoDTO dto, Invoice invoice){
        //TODO : make pcc client and second bank
        Account seller = accountRepository.getById(invoice.getAccountId());
        BigDecimal amountToMove = exchangeService.exchange(invoice.getCurrency(), invoice.getAmount(), seller.getCurrency());

        PCCRequestDTO request = new PCCRequestDTO(invoice.getId(), invoice.getTransaction().getCreated(), dto.getPan(), dto.getCardHolderName(), dto.getExpirationDate(), dto.getSecurityCode(), amountToMove, seller.getCurrency().toString(), seller.getId());
        PCCResponseDTO response = pccClient.bankPaymentResponse(URI.create("${pcc.transfer}"),request);

        Transaction transaction = transactionRepository.save(new Transaction(invoice, response.getFromId(), seller.getId()));
        invoice.setTransaction(transaction);
        invoiceRepository.save(invoice);
        seller.setBalance(seller.getBalance().add(amountToMove));
        accountRepository.save(seller);

        return invoice;
    }

    public PCCResponseDTO receiveRequestFromPcc (PCCRequestDTO request) throws CreditCardNotFoundException {
        CreditCard card = cardService.getByPAN(request.getPanNumber());
        if(!isCardInThisBank(card.getPAN()))
            throw new CreditCardNotFoundException();
        Account buyer = accountRepository.getById(card.getAccountId());

        if (buyer.getBalance().compareTo(request.getAmount()) > 0) {
            Transaction transaction = transactionRepository.save(new Transaction( buyer.getId(), request.getToId(), request.getAmount(), request.getCurrency(), LocalDateTime.now()));

            buyer.setBalance(buyer.getBalance().subtract(request.getAmount()));
            accountRepository.save(buyer);
            PCCResponseDTO response = new PCCResponseDTO("SUCCESS", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), transaction.getCreated(), buyer.getId());
            return response;
        } else {
            PCCResponseDTO response = new PCCResponseDTO("NO_MONEY", request.getAcquirerOrderId(), request.getAcquirerTimeStamp(), request.getToId(), LocalDateTime.now(), buyer.getId());
            return response;
        }

    }

    private boolean isCardInThisBank(String pan){
        String cardBankNumber = pan.substring(1, 6);
        return cardBankNumber.equals(BANK_NUMBER);
    }
}
