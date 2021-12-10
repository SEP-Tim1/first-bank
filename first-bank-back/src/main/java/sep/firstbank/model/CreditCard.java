package sep.firstbank.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_generator")
    @SequenceGenerator(name="card_generator", sequenceName = "card_seq", initialValue = 10)
    private long id;

    @Column(name = "card_holder_name")
    @NotNull
    private String cardHolderName;

    @Column(name = "pan", length = 16, unique = true)
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Invalid PAN number")
    //@CreditCardNumber(message = "Invalid PAN number")
    private String PAN;

    @Column(name = "security_code", length = 3)
    @NotNull
    @Pattern(regexp = "\\d{3}", message = "Invalid CVC number")
    private String securityCode;

    @Column(name="expiration_date")
    @NotNull
    private LocalDate expirationDate;

    @Column(name = "account_id")
    private long accountId;

    public CreditCard() {
        super();
    }

    public CreditCard(long id, String cardHolderName, String PAN, String securityCode, LocalDate expirationDate) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.PAN = PAN;
        this.securityCode = securityCode;
        this.expirationDate = expirationDate;
    }

    public CreditCard(long id, String cardHolderName, String PAN, String securityCode, LocalDate expirationDate, long accountId) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.PAN = PAN;
        this.securityCode = securityCode;
        this.expirationDate = expirationDate;
        this.accountId = accountId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
