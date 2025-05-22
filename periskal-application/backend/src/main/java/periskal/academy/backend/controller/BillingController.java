package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.service.BillingService;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public ResponseEntity<Billing> saveOrder(@RequestBody Billing entity) {
        Billing savedOrder = billingService.saveOrder(entity);
        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/batch") 
    public ResponseEntity<List<Billing>> saveOrdersBatch(@RequestBody List<Billing> billingList) {
        List<Billing> savedOrders = billingService.saveAttemptsBatch(billingList);
        return ResponseEntity.ok(savedOrders);
    }

}
