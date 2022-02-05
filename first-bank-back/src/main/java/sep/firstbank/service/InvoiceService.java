package sep.firstbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sep.firstbank.clients.PSPClient;
import sep.firstbank.dtos.*;
import sep.firstbank.exceptions.CurrencyUnsupportedException;
import sep.firstbank.exceptions.InvoiceNotFoundException;
import sep.firstbank.model.Account;
import sep.firstbank.model.Invoice;
import sep.firstbank.repositories.AccountRepository;
import sep.firstbank.repositories.InvoiceRepository;

import javax.security.auth.login.AccountNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;

@Slf4j
@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;
    private final ExchangeService exchangeService;
    private final PSPClient pspClient;
    @Value("${front.url}")
    private String frontUrl;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, AccountRepository accountRepository, ExchangeService exchangeService, PSPClient pspClient) {
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
        this.exchangeService = exchangeService;
        this.pspClient = pspClient;
    }

    public InvoiceResponseDTO generateResponse(InvoiceDTO dto) throws AccountNotFoundException, CurrencyUnsupportedException {
        Account account = validate(dto);
        if (!exchangeService.conversionSupported(dto.getCurrency(), account.getCurrency())) {
            log.info("Invalid currency conversion attempt (" + dto.getCurrency() + " to " + account.getCurrency() + ')');
            throw new CurrencyUnsupportedException("Invoice currency unsupported");
        }
        Invoice invoice = invoiceRepository.save(new Invoice(dto, account));
        log.info("Invoice (id=" + invoice.getId() + ") for account (id=" + account.getId() + ") created");
        return new InvoiceResponseDTO(frontUrl + "make-payment/" + invoice.getId(), invoice.getId());
    }

    public Account validate(InvoiceDTO dto) throws AccountNotFoundException {
        Account account = accountRepository.findByMerchantIdAndMerchantPassword(dto.getMId(), dto.getMPassword());
        if (account == null) {
            throw new AccountNotFoundException();
        }
        return account;
    }

    public RedirectUrlDTO notifySuccess(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("SUCCESS", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), invoice.getTransaction().getCreated(), invoice.getId(), message);
        notifyPSP(dto, invoice.getCallbackUrl());
        return new RedirectUrlDTO(invoice.getSuccessUrl());
    }

    public RedirectUrlDTO notifyError(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("ERROR", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), LocalDateTime.now(), invoice.getId(), message);
        notifyPSP(dto, invoice.getCallbackUrl());
        return new RedirectUrlDTO(invoice.getErrorUrl());
    }

    public RedirectUrlDTO notifyFailure(Invoice invoice, String message){
        PaymentResponseDTO dto = new PaymentResponseDTO("FAILURE", invoice.getMerchantOrderId(), invoice.getRequestId(), invoice.getId(), LocalDateTime.now(), invoice.getId(), message);
        notifyPSP(dto, invoice.getCallbackUrl());
        return new RedirectUrlDTO(invoice.getFailureUrl());
    }

    public void notifyPSP(PaymentResponseDTO response, String url) {
        try{
            pspClient.bankPaymentResponse(URI.create(url), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InvoiceCustomerInfoDTO get(long id) throws InvoiceNotFoundException {
         Invoice invoice = invoiceRepository.findById(id).orElse(null);
         if (invoice == null) {
             throw new InvoiceNotFoundException();
         }
         InvoiceCustomerInfoDTO result = new InvoiceCustomerInfoDTO();
         result.setAmount(invoice.getAmount().longValue());
         result.setCurrency(invoice.getCurrency().toString());
         return result;
    }
}
