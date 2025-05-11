package periskal.academy.backend;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import periskal.academy.backend.service.certificate.CertificateService;
import periskal.academy.backend.util.CertificateGeneratorUtil;

class CertificateServiceTest {

    @Test
    void createCertificate_callsUtil_andReturnsBytes() throws IOException {

        //arrange
        CertificateService service = new CertificateService();
        byte[] fakePdfBytes = new byte[] { 1, 2, 3 };

        
        try (MockedStatic<CertificateGeneratorUtil> mockedUtil = mockStatic(CertificateGeneratorUtil.class)) {

            mockedUtil.when(() -> CertificateGeneratorUtil.generateCertificatePdf(
                    anyString(), anyString(), anyString(), anyString(),
                    anyList(), anyString(), anyString(), anyString(),
                    anyString(), anyString())).thenReturn(fakePdfBytes);

            // Act
            byte[] result = service.createCertificate();

            // Assert
            assertThat(result).isEqualTo(fakePdfBytes);

            // Assert 
            mockedUtil.verify(() -> CertificateGeneratorUtil.generateCertificatePdf(
                    anyString(), anyString(), anyString(), anyString(),
                    anyList(), anyString(), anyString(), anyString(),
                    anyString(), anyString()));
        }
    }
}
