package sep.firstbank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep.firstbank.model.Currency;
import sep.firstbank.model.ExchangeRate;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    boolean existsBySrcAndDest(Currency src, Currency dest);

    Optional<ExchangeRate> findBySrcAndDest(Currency src, Currency dest);
}
