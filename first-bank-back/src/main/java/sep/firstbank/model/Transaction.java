package sep.firstbank.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime created;

    public Transaction(Invoice invoice, long fromId, long toId){
        this.fromId = fromId;
        this.toId = toId;
        this.amount = invoice.getAmount();
        this.currency = invoice.getCurrency().toString();
        this.created = LocalDateTime.now();
    }


}
