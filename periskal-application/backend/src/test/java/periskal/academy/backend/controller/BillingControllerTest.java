package periskal.academy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.BillingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @Test
    void givenValidBilling_whenSaveOrder_thenReturnsOkWithSavedBilling() throws Exception {
    
    // Arrange
    when(billingService.saveOrder(any(Billing.class))).thenReturn(testBilling);

    // Act & Assert
    mockMvc.perform(post("/api/billing")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)  
            .content(objectMapper.writeValueAsString(testBilling)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.fullName").value("Test Customer"))
            .andExpect(jsonPath("$.examId").value("1"))
            .andExpect(jsonPath("$.email").value("hallo@test.be"));

    verify(billingService, times(1)).saveOrder(any(Billing.class));
    }

    @Test
    void givenValidBillingList_whenSaveOrdersBatch_thenReturnsOkWithSavedBillingList() throws Exception {
    
    // Arrange
    Billing billing2 = new Billing();
    billing2.setId(2L);
    billing2.setFullName("Test Customer 2");
    billing2.setExamId("2");
    billing2.setEmail("test2@test.be");

    List<Billing> billingList = Arrays.asList(testBilling, billing2);
    when(billingService.saveAttemptsBatch(ArgumentMatchers.<Billing>anyList())).thenReturn(billingList);


    // Act & Assert
    mockMvc.perform(post("/api/billing/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)  
            .content(objectMapper.writeValueAsString(billingList)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].fullName").value("Test Customer"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].fullName").value("Test Customer 2"));

    verify(billingService, times(1)).saveAttemptsBatch(ArgumentMatchers.<Billing>anyList());
    }

    @Test
    void givenEmptyRequestBody_whenSaveOrder_thenReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/billing")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
    }
}