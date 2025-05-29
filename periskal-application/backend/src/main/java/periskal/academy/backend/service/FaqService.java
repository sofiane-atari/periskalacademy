package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.faq.Faq;
import periskal.academy.backend.jpa.faq.FaqRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

@Slf4j
@Service
public class FaqService {

    
    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAllFaqs() {
        try {
            List<Faq> faqs = faqRepository.findAll();
            log.info("Retrieved {} FAQs from database", faqs.size());
            return faqs;
        } catch (DataAccessException e) {
            log.error("Failed to retrieve FAQs: {}", e.getMessage(), e);
            throw new RuntimeException("Could not fetch FAQs.", e);
        }
    }

    public Optional<Faq> getFaqById(Long id) {
        try {
            Optional<Faq> faq = faqRepository.findById(id);
            if (!faq.isPresent()) {
                log.warn("FAQ with id {} not found", id);
                throw new ResourceNotFoundException("FAQ not found with id " + id);
            }
            log.info("Retrieved FAQ with id {}", id);
            return faq;
        } catch (DataAccessException e) {
            log.error("Failed to fetch FAQ with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not fetch FAQ with id " + id, e);
        }
    }

    public Faq createFaq(Faq faq) {
        try {
            validateFaq(faq);
            Faq savedFaq = faqRepository.save(faq);
            log.info("Created new FAQ with id {}", savedFaq.getId());
            return savedFaq;
        } catch (ValidationException e) {
            log.warn("Validation failed for new FAQ: {}", e.getDetails());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating FAQ: {}", e.getMessage(), e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to create FAQ: {}", e.getMessage(), e);
            throw new RuntimeException("Could not create FAQ", e);
        }
    }

    public Faq updateFaq(Long id, Faq updatedFaq) {
        try {
            Optional<Faq> existingFaq = faqRepository.findById(id);
            if (!existingFaq.isPresent()) {
                log.warn("FAQ with id {} not found for update", id);
                throw new ResourceNotFoundException("FAQ not found with id " + id);
            }

            validateFaq(updatedFaq);
            Faq faq = existingFaq.get();
            faq.setQuestion(updatedFaq.getQuestion());
            faq.setAnswer(updatedFaq.getAnswer());

            Faq savedFaq = faqRepository.save(faq);
            log.info("Updated FAQ with id {}", savedFaq.getId());
            return savedFaq;
        } catch (ValidationException e) {
            log.warn("Validation failed for updating FAQ with id {}: {}", id, e.getDetails());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to update FAQ with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update FAQ with id " + id, e);
        }
    }

    public void deleteFaq(Long id) {
        try {
            if (!faqRepository.existsById(id)) {
                log.warn("FAQ with id {} not found for deletion", id);
                throw new ResourceNotFoundException("FAQ not found with id " + id);
            }
            faqRepository.deleteById(id);
            log.info("Deleted FAQ with id {}", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete FAQ with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete FAQ with id " + id, e);
        }
    }

    private void validateFaq(Faq faq) {
        List<String> errors = new ArrayList<>();
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty()) {
            errors.add("Question cannot be empty");
        }
        if (faq.getAnswer() == null || faq.getAnswer().trim().isEmpty()) {
            errors.add("Answer cannot be empty");
        }
        if (!errors.isEmpty()) {
            log.warn("Validation errors for FAQ: {}", errors);
            throw new ValidationException("Invalid FAQ data", errors);
        }
    }
}
