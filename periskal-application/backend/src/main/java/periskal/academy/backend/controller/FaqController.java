package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.faq.Faq;
import periskal.academy.backend.security.SecurityRoles;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.service.FaqService;

@RestController
@RequestMapping("/api/faq")
@Slf4j
public class FaqController {
    
    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<List<Faq>> getAllFaqs() {
        log.info("Received request to retrieve all FAQs");
        List<Faq> faqs = faqService.getAllFaqs();
        log.info("Successfully retrieved {} FAQs", faqs.size());
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/{id}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<Faq> getFaqById(@PathVariable Long id) {
        log.info("Received request to retrieve FAQ with id {}", id);
        Faq faq = faqService.getFaqById(id).orElseThrow(() -> {
            log.warn("FAQ with id {} not found", id);
            return new ResourceNotFoundException("FAQ not found with id " + id);
        });
        log.info("Successfully retrieved FAQ with id {}", id);
        return ResponseEntity.ok(faq);
    }

    @PostMapping
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        log.info("Received request to create FAQ with question: {}", faq.getQuestionEn());
        log.info("Received request to create FAQ with category: {}", faq.getCategory());
        Faq createdFaq = faqService.createFaq(faq);
        log.info("Successfully created FAQ with id {}", createdFaq.getId());
        return ResponseEntity.ok(createdFaq);
    }

    @PutMapping("/{id}")
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Faq> updateFaq(@PathVariable Long id, @RequestBody Faq faq) {
        log.info("Received request to update FAQ with id {}", id);
        Faq updatedFaq = faqService.updateFaq(id, faq);
        log.info("Successfully updated FAQ with id {}", id);
        return ResponseEntity.ok(updatedFaq);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        log.info("Received request to delete FAQ with id {}", id);
        faqService.deleteFaq(id);
        log.info("Successfully deleted FAQ with id {}", id);
        return ResponseEntity.noContent().build();
    }
}