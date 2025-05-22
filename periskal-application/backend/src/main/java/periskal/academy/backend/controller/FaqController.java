package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.faq.Faq;
import periskal.academy.backend.service.FaqService;

import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;

@RestController
@RequestMapping("/api/faq")
@Slf4j
public class FaqController {
     @Autowired
    private FaqService faqService;

    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        log.info("Received request to retrieve all FAQs");
        List<Faq> faqs = faqService.getAllFaqs();
        log.info("Successfully retrieved {} FAQs", faqs.size());
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        log.info("Received request to create FAQ with question: {}", faq.getQuestion());
        Faq createdFaq = faqService.createFaq(faq);
        log.info("Successfully created FAQ with id {}", createdFaq.getId());
        return ResponseEntity.ok(createdFaq);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faq> updateFaq(@PathVariable Long id, @RequestBody Faq faq) {
        log.info("Received request to update FAQ with id {}", id);
        Faq updatedFaq = faqService.updateFaq(id, faq);
        log.info("Successfully updated FAQ with id {}", id);
        return ResponseEntity.ok(updatedFaq);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        log.info("Received request to delete FAQ with id {}", id);
        faqService.deleteFaq(id);
        log.info("Successfully deleted FAQ with id {}", id);
        return ResponseEntity.noContent().build();
    }
}