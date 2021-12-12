package sep.firstbank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep.firstbank.clients.PSPClient;
import sep.firstbank.dtos.CardInfoDTO;
import sep.firstbank.dtos.MerchantCredentialsDTO;
import sep.firstbank.dtos.PaymentResponseDTO;
import sep.firstbank.dtos.RedirectUrlDTO;
import sep.firstbank.exceptions.*;
import sep.firstbank.model.Account;
import sep.firstbank.model.Invoice;
import sep.firstbank.service.AccountService;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("account")
public class AccountController {

    private final AccountService accountService;
    private final PSPClient pspClient;

    public AccountController(AccountService accountService, PSPClient pspClient) {
        this.accountService = accountService;
        this.pspClient = pspClient;
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try {
            Account account = accountService.getById(id);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (AccountNotFoundException e){
            return ResponseEntity.badRequest().body("Account does not exist.");
        }
    }

    @PostMapping(value = "register")
    public ResponseEntity<?> register(@Valid @RequestBody CardInfoDTO dto){
        try {
            return ResponseEntity.ok(accountService.register(dto));
        } catch (CreditCardNotFoundException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CreditCardInfoNotValidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "payment/{invoiceId}")
    public ResponseEntity<?> pay(@Valid @RequestBody CardInfoDTO dto, @PathVariable long invoiceId){
        try {
            Invoice invoice = accountService.getInvoice(dto, invoiceId);
            try {
                invoice = accountService.pay(dto, invoice);
                return ResponseEntity.ok(notifySuccess(invoice));
            } catch (InvoiceAlreadyPaidException | NoMoneyException e) {
                return ResponseEntity.ok(notifyFailure(invoice));
            }
        } catch (CreditCardInfoNotValidException | InvoiceNotFoundException | CreditCardNotFoundException e) {
            return ResponseEntity.ok(notifyError());
        }
    }

    @PostMapping("validate")
    public void validate(@RequestBody MerchantCredentialsDTO dto) throws AccountNotFoundException {
        accountService.validate(dto);
    }

    private RedirectUrlDTO notifySuccess(Invoice invoice){
        PaymentResponseDTO dto = new PaymentResponseDTO("SUCCESS", invoice.getMerchantOrderId(), invoice.getId(), invoice.getTransaction().getCreated(), invoice.getId());
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO(invoice.getSuccessUrl());
    }

    private RedirectUrlDTO notifyError(){
        PaymentResponseDTO dto = new PaymentResponseDTO("ERROR", 0, 0, LocalDateTime.now(), 0);
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO("");
    }

    private RedirectUrlDTO notifyFailure(Invoice invoice){
        PaymentResponseDTO dto = new PaymentResponseDTO("FAILURE", invoice.getMerchantOrderId(), invoice.getId(), LocalDateTime.now(), invoice.getId());
        try{
            pspClient.bankPaymentResponse(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectUrlDTO(invoice.getFailureUrl());
    }
}
