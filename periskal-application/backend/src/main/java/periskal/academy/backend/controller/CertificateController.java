package periskal.academy.backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.CertificateDTO;
import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;
import periskal.academy.backend.service.CertificateService;
import periskal.academy.backend.service.certificate.PdfGeneratorService;

@Slf4j
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    
    private final CertificateService certificateService;
    private final PdfGeneratorService pdfGeneratorService;
    
    public CertificateController(CertificateService certificateService, PdfGeneratorService pdfGeneratorService) {
        this.certificateService = certificateService;
        this.pdfGeneratorService = pdfGeneratorService;
    }
    

    //Get all certificates for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateDTO>> getUserCertificates(@PathVariable String userId) {
        List<CertificateDTO> certificates = certificateService.getCertificatesByUserId(userId);
        return ResponseEntity.ok(certificates);
    }
    
    // Save a new certificate or update an existing one
    @PostMapping("/save")
    public ResponseEntity<CertificateDTO> saveCertificate(@RequestBody CertificateDTO certificateDTO) {
        CertificateDTO savedCertificate = certificateService.saveCertificate(certificateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCertificate);
    }
    
    // Get a certificate by ID
    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable Long id) {
        CertificateDTO certificate = certificateService.getCertificateById(id);
        if (certificate == null) {
            throw new ResourceNotFoundException("Certificate not found with id: " + id);
        }
        return ResponseEntity.ok(certificate);
    }
    
    // Generate a certificate PDF
    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> generateCertificate(
            @PathVariable Long id, 
            @RequestParam String firstName, 
            @RequestParam String lastName,
            @RequestParam String shipName) {
                   
        // Get the certificate entity
        Certificate certificate = certificateService.getCertificateEntityById(id);
        if (certificate == null) {
            throw new ResourceNotFoundException("Certificate not found with id: " + id);
        }
        if (!certificate.isReadyForDownload()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // Use the PdfGeneratorService to generate a certificate 
            byte[] pdfBytes = pdfGeneratorService.createCertificate(certificate, firstName, lastName, shipName);
            
            // Set up the headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = certificate.getName().replace(" ", "_") + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error generating certificate PDF for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
