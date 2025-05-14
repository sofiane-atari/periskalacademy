package periskal.academy.backend.controller;

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

import periskal.academy.backend.controller.CertificateController;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.Certificate.CertificateService;

@WebMvcTest(controllers = CertificateController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CertificateService certificateService;

    @MockitoBean
    private TrainingRepository trainingRepository; // Mocked for completeness, not used in this test, Don't delete!

    private byte[] testPdfContent;

    @BeforeEach
    void setUp() {
        testPdfContent = "Test PDF Content".getBytes();
    }

    @Test
    void whenCreateCertificate_thenReturnsPdfWithCorrectHeaders() throws Exception {

        // Arrange
        when(certificateService.createCertificate()).thenReturn(testPdfContent);

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/certificate/generate")).andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(response.getHeader("Content-Disposition")).isEqualTo("attachment; filename=certificate.pdf");
        assertThat(response.getContentAsByteArray()).isEqualTo(testPdfContent);
    }
}
