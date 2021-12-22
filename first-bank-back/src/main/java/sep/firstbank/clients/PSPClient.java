package sep.firstbank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sep.firstbank.dtos.PaymentResponseDTO;

import java.net.URI;

@FeignClient(name="a", url="a")
public interface PSPClient {

    @PostMapping
    void bankPaymentResponse(URI baseUri, @RequestBody PaymentResponseDTO dto);
}
