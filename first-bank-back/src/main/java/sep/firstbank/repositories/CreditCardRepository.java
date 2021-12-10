package sep.firstbank.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sep.firstbank.model.CreditCard;

@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
    CreditCard findByPAN(String pan);
}