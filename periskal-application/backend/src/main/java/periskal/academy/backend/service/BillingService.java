package periskal.academy.backend.service;

import org.springframework.stereotype.Service;
import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.BillingRepository;

@Service
public class BillingService {

    private final BillingRepository orderRepository;

    public BillingService(BillingRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Billing saveOrder(Billing entity) {
        Billing saved = orderRepository.save(entity);
        return saved;
    }
}
