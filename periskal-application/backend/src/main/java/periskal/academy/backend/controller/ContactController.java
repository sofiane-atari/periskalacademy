package periskal.academy.backend.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import periskal.academy.backend.dto.ContactRequest;
import periskal.academy.backend.service.EmailService;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;
    
    @PostMapping
    @PreAuthorize("hasRole('SKIPPER')")
    public ResponseEntity<ContactRequest> submitContactForm(@Valid @RequestBody ContactRequest contactRequest) {    
        emailService.sendContactFormEmail(contactRequest);
        return ResponseEntity.ok(contactRequest);
        
    }
}
