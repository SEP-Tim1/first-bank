package sep.firstbank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep.firstbank.model.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    CreditCard findByPAN(String pan);
}