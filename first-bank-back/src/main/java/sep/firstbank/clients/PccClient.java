package sep.firstbank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sep.firstbank.dtos.PCCRequestDTO;
import sep.firstbank.dtos.PCCResponseDTO;

import java.net.URI;

@FeignClient(name="b", url="b")
public interface PccClient {

    @PostMapping
    PCCResponseDTO bankPaymentResponse(URI baseUri, @RequestBody PCCRequestDTO dto);
}
