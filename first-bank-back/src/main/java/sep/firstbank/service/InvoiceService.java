package sep.firstbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep.firstbank.dtos.InvoiceDTO;
import sep.firstbank.dtos.InvoiceResponseDTO;
import sep.firstbank.model.Account;
import sep.firstbank.model.Invoice;
import sep.firstbank.repositories.AccountRepository;
import sep.firstbank.repositories.InvoiceRepository;
import sep.firstbank.repositories.TransactionRepository;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private static final String PAYMENT_URL = "http://localhost:4300/make-payment/";

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public InvoiceResponseDTO generateResponse(InvoiceDTO dto) throws AccountNotFoundException {
        Account account = validate(dto);
        Invoice invoice = invoiceRepository.save(new Invoice(dto, account));
        return new InvoiceResponseDTO(PAYMENT_URL + invoice.getId(), invoice.getId());
    }

    public Account validate(InvoiceDTO dto) throws AccountNotFoundException {
        Account account = accountRepository.findByMerchantIdAndMerchantPassword(dto.getMId(), dto.getMPassword());
        if (account == null) throw new AccountNotFoundException();
        return account;
    }
}
