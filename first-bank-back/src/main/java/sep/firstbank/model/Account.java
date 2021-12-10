package sep.firstbank.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
    @SequenceGenerator(name="account_generator", sequenceName = "account_seq", initialValue = 10)
    private long id;

    @Column(name = "balance")
    @NotNull
    private BigDecimal balance;

    @Column(length = 3, name = "currency")
    @NotNull
    private String currency;

    @Column(unique = true, name = "m_id")
    private long merchantId;

    @Column(name = "m_password")
    private String merchantPassword;

    @OneToMany()
    private Set<CreditCard> cards = new HashSet<>();

    public Account(){ super();}

    public Account(BigDecimal balance, String currency) {
        this.balance = balance;
        this.currency = currency;
    }

    public Account(BigDecimal balance, String currency, Set<CreditCard> cards) {
        this.balance = balance;
        this.currency = currency;
        this.cards = cards;
    }

    public Account(long id, BigDecimal balance, String currency, long merchantId, String merchantPassword, Set<CreditCard> cards) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
        this.merchantId = merchantId;
        this.merchantPassword = merchantPassword;
        this.cards = cards;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }

    public Set<CreditCard> getCards() {
        return cards;
    }

    public void setCards(Set<CreditCard> cards) {
        this.cards = cards;
    }
}
