package sep.firstbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep.firstbank.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceDTO {
    private long mId;
    private String mPassword;
    private BigDecimal amount;
    private Currency currency;
    private long merchantOrderId;
    private long requestId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failureUrl;
    private String errorUrl;
    private String callbackUrl;
}

