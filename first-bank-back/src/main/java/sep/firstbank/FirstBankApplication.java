package sep.firstbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FirstBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstBankApplication.class, args);
    }

}
