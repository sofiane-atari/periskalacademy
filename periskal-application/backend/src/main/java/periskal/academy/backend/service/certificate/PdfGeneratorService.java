package periskal.academy.backend.service.certificate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.util.CertificateGeneratorUtil;

@Slf4j
@Service
public class PdfGeneratorService {


    // --- PDF Security ---
    @Value("${pdf.certificate.ownerPassword}") 
    private String pdfOwnerPassword; // Owner password for full access

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
        
        String productName = certificate.getTraining();
        
        String productDetails = certificate.getName();
        
        // Create a list with just the single crew name, could just be a string, less to chagne
        List<String> crewNames = Collections.singletonList(crewName);

        //signer details
        String signerName = "Marc Persoons";
        String signerTitle = "Managing Director, Periskal bv";
        String companyName = "PERISKAL bv";
        String companyAddress = "Den Isomo 11, 2990 Wuustwezel, Belgium";
        String companyPhone = "Phone +32 3 669 57 36";

        return CertificateGeneratorUtil.generateCertificatePdf(
                productName,
                productDetails,
                shipName,
                dateInfo,
                crewNames,
                signerName,
                signerTitle,
                companyName,
                companyAddress,
                companyPhone,
                ownerPassword);
    }
}
