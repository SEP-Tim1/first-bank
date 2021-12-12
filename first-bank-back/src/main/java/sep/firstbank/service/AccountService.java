package sep.firstbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep.firstbank.dtos.CardInfoDTO;
import sep.firstbank.dtos.MerchantCredentialsDTO;
import sep.firstbank.dtos.MerchantDTO;
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

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CreditCardService cardService;
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private static final long MIG = 603759;

    @Autowired
    public AccountService(AccountRepository accountRepository, CreditCardService cardService, InvoiceRepository invoiceRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.cardService = cardService;
        this.invoiceRepository = invoiceRepository;
        this.transactionRepository = transactionRepository;
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
        return new MerchantDTO(a.getMerchantId(), a.getMerchantPassword());
    }

    public Invoice getInvoice(CardInfoDTO dto, long invoiceId) throws CreditCardNotFoundException, CreditCardInfoNotValidException, InvoiceNotFoundException {
        if (!invoiceRepository.findById(invoiceId).isPresent()) throw new InvoiceNotFoundException();
        CreditCard card = cardService.getByPAN(dto.getPan());
        CreditCardValidator.validate(card, dto);
        return invoiceRepository.findById(invoiceId).get();
    }

    public Invoice pay(CardInfoDTO dto, Invoice invoice) throws InvoiceAlreadyPaidException, NoMoneyException, CreditCardNotFoundException {
        CreditCard card = cardService.getByPAN(dto.getPan());
        if(invoice.getTransaction() != null) throw new InvoiceAlreadyPaidException();
        return makeTransaction(card, invoice);
    }

    private Invoice makeTransaction(CreditCard card, Invoice invoice) throws NoMoneyException {
        Account buyer = accountRepository.getById(card.getAccountId());
        Account seller = accountRepository.getById(invoice.getAccountId());
        if (buyer.getBalance().compareTo(invoice.getAmount()) > 0) {
            Transaction transaction = transactionRepository.save(new Transaction(invoice, buyer.getId(), seller.getId()));
            invoice.setTransaction(transaction);
            invoiceRepository.save(invoice);
            buyer.setBalance(buyer.getBalance().subtract(invoice.getAmount()));
            seller.setBalance(seller.getBalance().add(invoice.getAmount()));
            accountRepository.save(buyer);
            accountRepository.save(seller);
            return invoice;
        } else {
            throw new NoMoneyException();
        }
    }
}
