package sep.firstbank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep.firstbank.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByMerchantIdAndMerchantPassword(long merchantId, String merchantPassword);
}
