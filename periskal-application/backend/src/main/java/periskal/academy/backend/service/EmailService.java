package periskal.academy.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ContactRequest;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") 
    private String fromEmailAddress;
    
    
    public void sendContactFormEmail(ContactRequest contactRequest) {

            String userId = contactRequest.getUserId();
            String firstName = contactRequest.getFirstName();
            String lastName = contactRequest.getLastName();
            String username = contactRequest.getUsername();
            String email = contactRequest.getEmail();
            String message = contactRequest.getMessage();
            
            String subject = userId + ": " + "Contact Form Submission from " + firstName + " " + lastName;
            String htmlContent = "<html><body>" +
                    "<h2>Contact Form Submission</h2>" +
                    "<p><strong>From:</strong> " + email + "</p>" +
                    "<p><strong>User ID:</strong> " + userId + "</p>" +
                    "<p><strong>Name:</strong> " + firstName + " " + lastName + "</p>" +
                    "<p><strong>Username:</strong> " + username + "</p>" +
                    "<h3>Message:</h3>" +
                    "<p>" + message + "</p>" +
                    "</body></html>";
            
            sendHtmlEmail(email, fromEmailAddress, subject, htmlContent);
           
    }

    public void sendHtmlEmail(String from, String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setFrom(from); // Specific 'from' address
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully from {} to {} with subject '{}'", from, to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email from {} to {} with subject '{}'. Error: {}", from, to, subject, e.getMessage(), e);
        }
    }
}