package sep.firstbank.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep.firstbank.util.SensitiveDataConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
    @SequenceGenerator(name="account_generator", sequenceName = "account_seq", initialValue = 10)
    private long id;

    @Column(name = "balance")
    @NotNull
    private BigDecimal balance;

    @Column
    @NotNull
    private Currency currency;

    @Column(unique = true, name = "m_id")
    private long merchantId;

    @Column(name = "m_password")
    //@Convert(converter = SensitiveDataConverter.class)
    private String merchantPassword;

    @OneToMany()
    private Set<CreditCard> cards = new HashSet<>();
}
