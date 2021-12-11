package sep.firstbank.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_generator")
    @SequenceGenerator(name="transaction_generator", sequenceName = "transaction_seq", initialValue = 10)
    private long id;

    @Column
    private long fromId;

    @Column
    private long toId;

    @Column
    @NotNull
    private BigDecimal amount;

    @Column
    private String currency;

    @Column
    private LocalDate created;

}
