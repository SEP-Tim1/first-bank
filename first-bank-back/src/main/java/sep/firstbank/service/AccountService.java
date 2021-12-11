package sep.firstbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep.firstbank.dtos.CardInfoDTO;
import sep.firstbank.dtos.MerchantCredentialsDTO;
import sep.firstbank.dtos.MerchantDTO;
import sep.firstbank.exceptions.CreditCardInfoNotValidException;
import sep.firstbank.exceptions.CreditCardNotFoundException;
import sep.firstbank.model.Account;
import sep.firstbank.model.CreditCard;
import sep.firstbank.repositories.AccountRepository;
import sep.firstbank.util.CreditCardValidator;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CreditCardService cardService;
    private static final long MIG = 603759;

    @Autowired
    public AccountService(AccountRepository accountRepository, CreditCardService cardService){
        this.accountRepository = accountRepository;
        this.cardService = cardService;
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
}
