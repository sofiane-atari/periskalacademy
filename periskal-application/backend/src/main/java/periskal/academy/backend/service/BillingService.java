package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.BillingRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@Service
@Slf4j
public class BillingService {

    private final BillingRepository billingRepository;
    private final UserExamAttemptRepository userExamAttemptRepository;
    private final EmailService emailService;
    private final ExamService examService;

    @Value("${spring.mail.username}")
    private String companyEmail;

    public BillingService(BillingRepository billingRepository, UserExamAttemptRepository userExamAttemptRepository,
            EmailService emailService, ExamService examService) {
        this.billingRepository = billingRepository;
        this.userExamAttemptRepository = userExamAttemptRepository;
        this.emailService = emailService;
        this.examService = examService;
    }

    public Billing saveOrder(Billing entity) {
        Billing saved = billingRepository.save(entity);
        saveAttempts(entity);

        sendBillingConfirmationEmail(saved);

        return saved;
    }

    public String saveAttempts(Billing entity) {
        UserExamAttempt newUserExamAttempt = new UserExamAttempt();
        newUserExamAttempt.setExamId(entity.getExamId());
        newUserExamAttempt.setTotalAttempts(entity.getExamAttempts());
        newUserExamAttempt.setUsedAttempts(0);
        newUserExamAttempt.setUserId(entity.getUserId());
        userExamAttemptRepository.save(newUserExamAttempt);
        return "ok";
    }

    public List<Billing> saveAttemptsBatch(List<Billing> entity) {
        List<Billing> savedBillingEntities = (List<Billing>) billingRepository.saveAll(entity);

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

        sendFleetManagerBatchOrderEmail(savedBillingEntities.size(), entity.get(0).getExamId());

        return savedBillingEntities;
    }

    private void sendBillingConfirmationEmail(Billing billing) {
        if (billing.getEmail() == null || billing.getEmail().isEmpty()) {
            log.warn("Cannot send billing email: recipient email is missing for order ID {}", billing.getId());
            return;
        }

        String to = companyEmail;
        String subject = "Billing Information - Order #" + billing.getId();

        Optional<Exam> examOptional = examService.findById(Long.valueOf(billing.getExamId()));
        Exam exam = examOptional.get();
        String examName;
        examName = exam.getExamTitle();

        // Constructing a simple HTML email content
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><body>");
        contentBuilder.append("<h2>Order From, ").append(billing.getFullName()).append("!</h2>");
        contentBuilder.append("<p>Here is a summary of the billing information:</p>");
        contentBuilder.append("<ul>");
        contentBuilder.append(listItem("Full Name", billing.getFullName()));
        contentBuilder.append(listItem("Company Name", billing.getCompanyName()));
        contentBuilder.append(listItem("Address", billing.getAddress()));
        contentBuilder.append(listItem("City", billing.getCity()));
        contentBuilder.append(listItem("State", billing.getState()));
        contentBuilder.append(listItem("Zip Code", billing.getZipCode()));
        contentBuilder.append(listItem("Country", billing.getCountryCode()));
        contentBuilder.append("</ul>");
        contentBuilder.append("<p><strong>Tax Information:</strong> ")
                .append(billing.getTax() != null ? billing.getTax() : "N/A").append("</p>");
        contentBuilder.append("<p>1 Exam of ").append(examName).append("</p>");
        contentBuilder.append("<p>Price is:</p>");
        contentBuilder.append("</body></html>");

        String content = contentBuilder.toString();

        emailService.sendHtmlEmail(billing.getEmail(), to, subject, content);
        log.info("Billing confirmation email sent to {} for order ID {}", to, billing.getId());
    }

    private String listItem(String label, String value) {
        return "<li><strong>" + label + ":</strong> " + (value != null ? value : "N/A") + "</li>";
    }

    private void sendFleetManagerBatchOrderEmail(int numberOfBillings, String examId) {
        if (numberOfBillings <= 0) {
            log.warn("Not sending fleet manager email: number of billings is zero or negative.");
            return;
        }
        if (examId == null || examId.isEmpty()) {
            log.warn("Not sending fleet manager email: Exam ID is missing for the batch order.");
            return;
        }

        String fromAddress = companyEmail; // Email is from the system
        String toAddress = companyEmail; // Email is to the fleet manager (using companyEmail as placeholder)
        String subject = "Fleet Manager Batch Order Processed - Exam: " + examId;

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><body>");
        contentBuilder.append("<h2>Fleet Manager Batch Order Notification</h2>");
        contentBuilder.append("<p>A batch order has been successfully processed.</p>");
        contentBuilder.append("<ul>");
        contentBuilder.append(listItem("Exam ID", examId));
        contentBuilder.append(listItem("Number of Users/Billings Processed", String.valueOf(numberOfBillings)));
        contentBuilder.append("</ul>");
        contentBuilder.append("<p>User exam attempts have been created accordingly.</p>");
        contentBuilder.append("<p>Thank you.</p>");
        contentBuilder.append("</body></html>");

        String content = contentBuilder.toString();

        try {
            emailService.sendHtmlEmail(fromAddress, toAddress, subject, content);
            log.info("Fleet manager batch order email sent to {} (from {}) for exam ID {} ({} users).", toAddress,
                    fromAddress, examId, numberOfBillings);
        } catch (Exception e) {
            log.error("Failed to send fleet manager batch order email for exam ID {} to {}: {}", examId, toAddress,
                    e.getMessage(), e);
        }
    }

}
