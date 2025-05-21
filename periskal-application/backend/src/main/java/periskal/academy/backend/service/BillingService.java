package periskal.academy.backend.service;

import org.springframework.stereotype.Service;
import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.BillingRepository;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final UserExamAttemptRepository userExamAttemptRepository;

    public BillingService(BillingRepository billingRepository, UserExamAttemptRepository userExamAttemptRepository) {
        this.billingRepository = billingRepository;
        this.userExamAttemptRepository = userExamAttemptRepository;
    }

    public Billing saveOrder(Billing entity) {
        Billing saved = billingRepository.save(entity);
        SaveAttempts(entity);
        return saved;
    }

    public String SaveAttempts(Billing entity) {
        UserExamAttempt newUserExamAttempt = new UserExamAttempt();
        newUserExamAttempt.setExamId(entity.getExamId());
        newUserExamAttempt.setTotalAttempts(entity.getExamAttempts());
        newUserExamAttempt.setUsedAttempts(0);
        newUserExamAttempt.setUserId(entity.getUserId());
        userExamAttemptRepository.save(newUserExamAttempt);
        return "ok";
    }
}
