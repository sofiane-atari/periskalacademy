package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.List;

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
        newUserExamAttempt.setUsedAttempts(1);
        newUserExamAttempt.setUserId(entity.getUserId());
        userExamAttemptRepository.save(newUserExamAttempt);
        return "ok";
    }

     public List<Billing> saveAttemptsBatch(List<Billing> entity) {
        List<Billing> savedBillingEntities = new ArrayList<>();
        savedBillingEntities = (List<Billing>) billingRepository.saveAll(entity);

        List<UserExamAttempt> attemptsToSave = new ArrayList<>();
        for (Billing billing : entity) {
            UserExamAttempt newUserExamAttempt = new UserExamAttempt();
            newUserExamAttempt.setExamId(billing.getExamId());
            newUserExamAttempt.setTotalAttempts(billing.getExamAttempts());
            newUserExamAttempt.setUsedAttempts(0);
            newUserExamAttempt.setUserId(billing.getUserId());
            attemptsToSave.add(newUserExamAttempt);
        }

        userExamAttemptRepository.saveAll(attemptsToSave);
        return savedBillingEntities;
    }

    
}
