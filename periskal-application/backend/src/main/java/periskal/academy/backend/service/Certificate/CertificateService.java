package periskal.academy.backend.service.certificate;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.util.CertificateGeneratorUtil;

@Slf4j
@Service
public class CertificateService {

    public byte[] createCertificate() throws IOException {
        // Dummy Data, later to be replaced with actual data from the database or JWT token
        String productName = "DEMO PRODUCT";
        String productDetails = "DEMO PRODUCT DETAILS";
        String shipName = "DEMO SHIPNAME";
        String trainingDate = java.time.LocalDate.now().toString();
        List<String> crewNames = List.of("DEMO CREW", "DEMO CREW2", "DEMO CREW3", "DEMO CREW4");
        String signerName = "Marc Persoons";
        String signerTitle = "Managing Director, Periskal bv";
        String companyName = "PERISKAL bv";
        String companyAddress = "Den Isomo 11, 2990 Wuustwezel, Belgium";
        String companyPhone = "Phone +32 3 669 57 36";


        return CertificateGeneratorUtil.generateCertificatePdf(
                productName,
                productDetails,
                shipName,
                trainingDate,
                crewNames,
                signerName,
                signerTitle,
                companyName,
                companyAddress,
                companyPhone
        );
    }
}