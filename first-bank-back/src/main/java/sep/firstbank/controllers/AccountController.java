package sep.firstbank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep.firstbank.dtos.CardInfoDTO;
import sep.firstbank.dtos.MerchantCredentialsDTO;
import sep.firstbank.exceptions.CreditCardInfoNotValidException;
import sep.firstbank.exceptions.CreditCardNotFoundException;
import sep.firstbank.model.Account;
import sep.firstbank.service.AccountService;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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

    @PostMapping("validate")
    public void validate(@RequestBody MerchantCredentialsDTO dto) throws AccountNotFoundException {
        accountService.validate(dto);
    }
}
