package sep.firstbank.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sep.firstbank.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
}
