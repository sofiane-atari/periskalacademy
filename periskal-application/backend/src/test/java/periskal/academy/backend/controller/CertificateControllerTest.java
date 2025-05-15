package periskal.academy.backend.controller;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import periskal.academy.backend.dto.CertificateDTO;
import periskal.academy.backend.jpa.Certificate;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.CertificateService;
import periskal.academy.backend.service.certificate.PdfGeneratorService;

@WebMvcTest(controllers = CertificateController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PdfGeneratorService pdfGeneratorService;

    @MockitoBean
    private CertificateService certificateService; 

    @MockitoBean
    private TrainingRepository trainingRepository; // Mocked for completeness, not used in this test, Don't delete!

    private byte[] testPdfContent;
    private CertificateDTO mockCertificateDto;
    private Certificate mockCertificate;

    @BeforeEach
    void setUp() {
        testPdfContent = "Test PDF Content".getBytes();

        // Set up certificate DTO
        mockCertificateDto = new CertificateDTO(
            1L,
            "Certificate POE",
            LocalDate.now(),
            LocalDate.now(),
            "Spring Boot Basics",
            true,
            "user123"
        );
        
        // Set up certificate entity
        mockCertificate = new Certificate();
        mockCertificate.setId(1L);
        mockCertificate.setName("Certificate POE");
        mockCertificate.setIssueDate(LocalDate.now());
        mockCertificate.setExpiryDate(LocalDate.now());
        mockCertificate.setTraining("Spring Boot Basics");
        mockCertificate.setReadyForDownload(true);
        mockCertificate.setUserId("user123");
    }

    @Test
    void whenGenerateCertificate_thenReturnsPdfWithCorrectHeaders() throws Exception { 
        
        // Arrange
        Long certificateId = 1L; 
        String expectedFilename = mockCertificateDto.getName().replace(" ", "_") + ".pdf";

        String expectedContentDispositionHeader = String.format("form-data; name=\"attachment\"; filename=\"%s\"", expectedFilename);

        when(certificateService.getCertificateById(certificateId)).thenReturn(mockCertificateDto);
        when(certificateService.getCertificateEntityById(certificateId)).thenReturn(mockCertificate);
        when(pdfGeneratorService.createCertificate(mockCertificate, "Certificate", "Holder", "Vessel"))
            .thenReturn(testPdfContent);

        // Act
        MvcResult mvcResult = mockMvc.perform(
            get("/api/certificate/generate/{id}", certificateId)
                .param("firstName", "Certificate")
                .param("lastName", "Holder")
                .param("shipName", "Vessel")
        ).andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(response.getHeader("Content-Disposition")).isEqualTo(expectedContentDispositionHeader);
        assertThat(response.getContentAsByteArray()).isEqualTo(testPdfContent);
    }
}
