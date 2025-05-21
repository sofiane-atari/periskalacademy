package periskal.academy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.BillingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingService billingService;

    @MockitoBean
    private TrainingRepository trainingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Billing testBilling;

    @BeforeEach
    void setUp() {
        testBilling = new Billing();
        testBilling.setId(1L);
        testBilling.setFullName("Test Customer");
        testBilling.setExamId("1");
        testBilling.setAddress("Test Street 1");
        testBilling.setCity("Antwerpen");
        testBilling.setEmail("hallo@test.be");
        testBilling.setTax("BE0123456789");
        testBilling.setZipCode("2000");
        testBilling.setState("Antwerpen");
        testBilling.setCompanyName("Test Company NV");
        testBilling.setUserId("PER00001");
        testBilling.setCountryCode("BE");
    }

    @Test
    void givenMalformedRequestBody_whenSaveOrder_thenReturnsBadRequest() throws Exception {
        String malformedJson = "{ \"id\": \"not-an-id\", \"fullName\": \"Invalid Data\" }";

        mockMvc.perform(post("/api/billing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}