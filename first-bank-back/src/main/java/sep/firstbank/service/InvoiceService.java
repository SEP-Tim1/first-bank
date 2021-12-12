package sep.firstbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep.firstbank.clients.PSPClient;
import sep.firstbank.dtos.InvoiceDTO;
import sep.firstbank.dtos.InvoiceResponseDTO;
import sep.firstbank.dtos.PaymentResponseDTO;
import sep.firstbank.dtos.RedirectUrlDTO;
import sep.firstbank.model.Account;
import sep.firstbank.model.Invoice;
import sep.firstbank.repositories.AccountRepository;
import sep.firstbank.repositories.InvoiceRepository;
import sep.firstbank.repositories.TransactionRepository;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PSPClient pspClient;
    private static final String PAYMENT_URL = "http://localhost:4300/make-payment/";

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, TransactionRepository transactionRepository, AccountRepository accountRepository, PSPClient pspClient) {
        this.invoiceRepository = invoiceRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.pspClient = pspClient;
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

    public RedirectUrlDTO notifySuccess(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("SUCCESS", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), invoice.getTransaction().getCreated(), invoice.getId(), message);
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO(invoice.getSuccessUrl());
    }

    public RedirectUrlDTO notifyError(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("ERROR", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), LocalDateTime.now(), invoice.getId(), message);
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO(invoice.getErrorUrl());
    }

    public RedirectUrlDTO notifyFailure(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("FAILURE", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), LocalDateTime.now(), invoice.getId(), message);
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO(invoice.getFailureUrl());
    }
}
