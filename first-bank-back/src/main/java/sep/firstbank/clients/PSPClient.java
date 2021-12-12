package sep.firstbank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sep.firstbank.dtos.PaymentResponseDTO;

@FeignClient(url= "localhost:8090", name = "psp")
public interface PSPClient {

    @PostMapping("card/card/bank-payment-response")
    void bankPaymentResponse(@RequestBody PaymentResponseDTO dto);
}
