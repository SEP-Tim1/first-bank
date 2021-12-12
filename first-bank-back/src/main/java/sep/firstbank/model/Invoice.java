package sep.firstbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep.firstbank.dtos.InvoiceDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_generator")
    @SequenceGenerator(name="invoice_generator", sequenceName = "invoice_seq", initialValue = 10)
    private long id;

    @Column
    private BigDecimal amount;

    @Column
    private long accountId;

    @Column
    private long merchantOrderId;

    @Column
    private long requestId;

    @Column
    private LocalDateTime merchantTimestamp;

    @Column
    private String successUrl;

    @Column
    private String failureUrl;

    @Column
    private String errorUrl;

    @OneToOne
    private Transaction transaction;

    public Invoice(InvoiceDTO dto, Account account){
        this.amount = dto.getAmount();
        this.accountId = account.getId();
        this.merchantOrderId = dto.getMerchantOrderId();
        this.requestId = dto.getRequestId();
        this.merchantTimestamp = dto.getMerchantTimestamp();
        this.successUrl = dto.getSuccessUrl();
        this.failureUrl = dto.getFailureUrl();
        this.errorUrl = dto.getErrorUrl();
    }

}
