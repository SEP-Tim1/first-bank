package sep.firstbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceDTO {
    private long mId;
    private String mPassword;
    private BigDecimal amount;
    private long merchantOrderId;
    private long requestId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failureUrl;
    private String errorUrl;
}

