package periskal.academy.backend.service;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.service.certificate.PdfGeneratorService;
import periskal.academy.backend.util.CertificateGeneratorUtil;

class PdfGeneratorServiceTest {

    private Certificate mockCertificate;
    private String testFirstName;
    private String testLastName;
    private String testShipName;

    @BeforeEach
    void setUp() {
        // Set up test data
        testFirstName = "John";
        testLastName = "Doe";
        testShipName = "Test Ship";
        
        // Create mock certificate
        mockCertificate = new Certificate();
        mockCertificate.setId(1L);
        mockCertificate.setName("Test Certificate");
        mockCertificate.setTraining("Test Training");
        mockCertificate.setIssueDate(LocalDate.now());
        mockCertificate.setExpiryDate(LocalDate.now().plusYears(1));
        mockCertificate.setReadyForDownload(true);
        mockCertificate.setUserId("user123");
    }

    @Test
    void createCertificate_callsUtil_andReturnsBytes() throws IOException {
        // arrange
        PdfGeneratorService service = new PdfGeneratorService();
        byte[] fakePdfBytes = new byte[] { 1, 2, 3 };

        try (MockedStatic<CertificateGeneratorUtil> mockedUtil = mockStatic(CertificateGeneratorUtil.class)) {
            mockedUtil.when(() -> CertificateGeneratorUtil.generateCertificatePdf(
                    anyString(), anyString(), anyString(), anyString(),
                    anyList(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString())).thenReturn(fakePdfBytes);

            // Act
            byte[] result = service.createCertificate(mockCertificate, testFirstName, testLastName, testShipName);

            // Assert
            assertThat(result).isEqualTo(fakePdfBytes);

            // Verify
            mockedUtil.verify(() -> CertificateGeneratorUtil.generateCertificatePdf(
                    anyString(), anyString(), anyString(), anyString(),
                    anyList(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString()));
        }
    }
}
