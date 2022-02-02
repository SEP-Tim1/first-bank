package sep.firstbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PCCResponseDTO {

    private String status;

    private long acquirerOrderId;

    private LocalDateTime acquirerTimestamp;

    private long issuerOrderId;

    private LocalDateTime issuerTimestamp;

    private long fromId;


}
