package sep.firstbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep.firstbank.exceptions.CreditCardNotFoundException;
import sep.firstbank.model.CreditCard;
import sep.firstbank.repositories.CreditCardRepository;

import java.time.LocalDate;

@Service
public class CreditCardService {
    private final CreditCardRepository cardRepository;

    @Autowired
    public CreditCardService(CreditCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CreditCard getByPAN(String PAN) throws CreditCardNotFoundException {
        CreditCard card = cardRepository.findByPAN(PAN);
        if(card != null) return card;
        throw new CreditCardNotFoundException();
    }
}
