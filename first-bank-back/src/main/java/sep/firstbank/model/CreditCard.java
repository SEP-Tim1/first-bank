package sep.firstbank.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep.firstbank.util.SensitiveDataConverter;

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

    @Column(name = "pan", unique = true)
    @NotNull
    @Convert(converter = SensitiveDataConverter.class)
    private String PAN;

    @Column(name = "security_code")
    @NotNull
    @Convert(converter = SensitiveDataConverter.class)
    private String securityCode;

    @Column(name="expiration_date")
    @NotNull
    private LocalDate expirationDate;

    @Column(name = "account_id")
    private long accountId;
}
