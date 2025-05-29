package periskal.academy.backend.service.certificate;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.CertificatePdfDTO;
import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.util.CertificateGeneratorUtil;

@Slf4j
@Service
public class PdfGeneratorService {


    // --- PDF Security ---
    @Value("${pdf.certificate.ownerPassword}") 
    private String pdfOwnerPassword; // Owner password for full access

    // Static constants for signer and company details
    private static final String SIGNER_NAME = "Marc Persoons";
    private static final String SIGNER_TITLE = "Managing Director, Periskal bv";
    private static final String COMPANY_NAME = "PERISKAL bv";
    private static final String COMPANY_ADDRESS = "Den Isomo 11, 2990 Wuustwezel, Belgium";
    private static final String COMPANY_PHONE = "Phone +32 3 669 57 36";

    public byte[] createCertificate(Certificate certificate, String firstName, String lastName, String shipName) throws IOException {
       
        // Format the user's full name
        String crewName = firstName + " " + lastName;

        String ownerPassword = pdfOwnerPassword; 
        
        String trainingDate = java.time.LocalDate.now().toString();
        
        // expiry date (valid until date)
        String validUntilDate;
        if (certificate.getExpiryDate() != null) {
            validUntilDate = "Valid until: " + certificate.getExpiryDate();
        } else {
            validUntilDate = "No expiration date";
        }
        
        // Combine the training date and valid until date
        String dateInfo = trainingDate + " | " + validUntilDate;
        
        CertificatePdfDTO dto = new CertificatePdfDTO(
            certificate.getTraining(),
            certificate.getName(),
            shipName,
            dateInfo,
            Collections.singletonList(crewName),
            SIGNER_NAME,
            SIGNER_TITLE,
            COMPANY_NAME,
            COMPANY_ADDRESS,
            COMPANY_PHONE
        );

        return CertificateGeneratorUtil.generateCertificatePdf(
                dto,
                ownerPassword);
    }
}
