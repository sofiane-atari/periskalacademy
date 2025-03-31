package periskal.academy.backend.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.service.Certificate.CertificateService;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    
    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> createCertificate() throws IOException {

        byte[] pdfBytes = certificateService.createCertificate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificate.pdf")
                .body(pdfBytes);
            
    }
}
