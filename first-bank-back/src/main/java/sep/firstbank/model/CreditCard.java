package sep.firstbank.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Data
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
}
