package sep.firstbank.dtos;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardInfoDTO {
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Invalid PAN number")
    private String pan;

    @NotNull
    private String cardHolderName;

    @NotNull
    @Size(min = 7, max = 7, message = "Invalid expiratory date")
    @Pattern(regexp = "\\d{2}/\\d{4}", message = "Invalid expiratory date")
    private String expirationDate;

    @NotNull
    @Pattern(regexp = "\\d{3}", message = "Invalid CVC number")
    private String securityCode;
}
