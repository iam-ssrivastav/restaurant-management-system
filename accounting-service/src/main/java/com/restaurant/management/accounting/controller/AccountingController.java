package com.restaurant.management.accounting.controller;

import com.restaurant.management.accounting.model.Invoice;
import com.restaurant.management.accounting.repository.InvoiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounting")
@Tag(name = "Accounting & Finance", description = "Invoice and payment processing")
public class AccountingController {

    private final InvoiceRepository repository;

    public AccountingController(InvoiceRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get all invoices")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<Invoice> getAllInvoices() {
        return repository.findAll();
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get a bill (invoice) by ID")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public Invoice getBill(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PatchMapping("/invoices/{id}/pay")
    @Operation(summary = "Process invoice payment")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public Invoice payInvoice(@PathVariable Long id) {
        Invoice invoice = repository.findById(id).orElseThrow();
        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        return repository.save(invoice);
    }
}
