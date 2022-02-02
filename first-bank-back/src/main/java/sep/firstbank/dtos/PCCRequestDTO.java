package sep.firstbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PCCRequestDTO {

    private long acquirerOrderId;

    private LocalDateTime acquirerTimeStamp;

    private String panNumber;

    private String cardHolderName;

    private String expiratoryDate;

    private String securityCode;

    private BigDecimal amount;

    private String currency;

    private long toId;



}
