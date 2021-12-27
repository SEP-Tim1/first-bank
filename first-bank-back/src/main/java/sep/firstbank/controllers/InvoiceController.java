package sep.firstbank.controllers;

import org.springframework.web.bind.annotation.*;
import sep.firstbank.dtos.InvoiceDTO;
import sep.firstbank.dtos.InvoiceResponseDTO;
import sep.firstbank.exceptions.CurrencyUnsupportedException;
import sep.firstbank.service.InvoiceService;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("generate")
    public InvoiceResponseDTO generate(@RequestBody InvoiceDTO dto) throws AccountNotFoundException, CurrencyUnsupportedException {
        return invoiceService.generateResponse(dto);
    };
}
