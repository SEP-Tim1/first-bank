package sep.firstbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MerchantCredentialsDTO {
    private long mid;
    private String mpassword;
}
