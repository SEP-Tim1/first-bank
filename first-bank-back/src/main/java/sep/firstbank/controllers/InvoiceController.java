package sep.firstbank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sep.firstbank.dtos.InvoiceCustomerInfoDTO;
import sep.firstbank.dtos.InvoiceDTO;
import sep.firstbank.dtos.InvoiceResponseDTO;
import sep.firstbank.exceptions.CurrencyUnsupportedException;
import sep.firstbank.exceptions.InvoiceNotFoundException;
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
    }

    @GetMapping("{id}")
    public InvoiceCustomerInfoDTO get(@PathVariable long id) {
        try {
            return invoiceService.get(id);
        } catch (InvoiceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice not found");
        }
    }
}
