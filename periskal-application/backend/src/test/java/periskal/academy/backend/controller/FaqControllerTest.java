package periskal.academy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.jpa.faq.Faq;
import periskal.academy.backend.service.FaqService;
import periskal.academy.backend.util.JwtUtil;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(FaqController.class)
@AutoConfigureMockMvc(addFilters = false)
class FaqControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private FaqService faqService;

        @MockitoBean
        private TrainingRepository trainingRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private JwtUtil jwtUtil;

        private Faq sampleFaq;
        private List<Faq> sampleFaqs;

        @BeforeEach
        void setUp() {
                sampleFaq = new Faq();
                sampleFaq.setId(1L);
                sampleFaq.setQuestionEn("What does it mean when a training is assigned by fleetmanager?");
                sampleFaq.setAnswerEn("This training has already been paid for by your fleetmanager");
                sampleFaq.setCategory("Training");

                Faq faq2 = new Faq();
                faq2.setId(2L);
                faq2.setQuestionEn("How can i achieve a certificate?");
                faq2.setAnswerEn("You can achieve a certificate by completing an exam with the required score");
                faq2.setCategory("Certification");

                sampleFaqs = Arrays.asList(sampleFaq, faq2);
        }

        // GET /api/faq - Get All FAQs Tests

        @Test
        void givenExistingFaqs_whenGetAllFaqs_thenReturnFaqList() throws Exception {
                // Given
                given(faqService.getAllFaqs()).willReturn(sampleFaqs);

                // When & Then
                MvcResult result = mockMvc.perform(get("/api/faq")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                List<Faq> returnedFaqs = Arrays.asList(objectMapper.readValue(responseContent, Faq[].class));

                assertThat(returnedFaqs)
                                .hasSize(2)
                                .extracting(Faq::getId)
                                .containsExactly(1L, 2L);
        }

        @Test
        void givenNoFaqs_whenGetAllFaqs_thenReturnEmptyList() throws Exception {
                // Given
                given(faqService.getAllFaqs()).willReturn(Arrays.asList());

                // When & Then
                MvcResult result = mockMvc.perform(get("/api/faq")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                List<Faq> returnedFaqs = Arrays.asList(objectMapper.readValue(responseContent, Faq[].class));

                assertThat(returnedFaqs).isEmpty();
        }

        // GET /api/faq/{id} - Get FAQ by ID Tests

        @Test
        void givenExistingFaqId_whenGetFaqById_thenReturnFaq() throws Exception {
                // Given
                Long faqId = 1L;
                given(faqService.getFaqById(faqId)).willReturn(Optional.of(sampleFaq));

                // When & Then
                MvcResult result = mockMvc.perform(get("/api/faq/{id}", faqId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                Faq returnedFaq = objectMapper.readValue(responseContent, Faq.class);

                assertThat(returnedFaq)
                                .isNotNull()
                                .satisfies(faq -> {
                                        assertThat(faq.getId()).isEqualTo(faqId);
                                        assertThat(faq.getQuestionEn()).isEqualTo(
                                                        "What does it mean when a training is assigned by fleetmanager?");
                                        assertThat(faq.getAnswerEn()).isEqualTo(
                                                        "This training has already been paid for by your fleetmanager");
                                        assertThat(faq.getCategory()).isEqualTo("Training");
                                });
        }

        @Test
        void givenNonExistingFaqId_whenGetFaqById_thenThrowResourceNotFoundException() throws Exception {
                // Given
                Long nonExistingId = 999L;
                given(faqService.getFaqById(nonExistingId)).willReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/faq/{id}", nonExistingId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        // POST /api/faq - Create FAQ Tests

        @Test
        void givenValidFaqData_whenCreateFaq_thenReturnCreatedFaq() throws Exception {
                // Given
                Faq newFaq = new Faq();
                newFaq.setQuestionEn("What is JUnit?");
                newFaq.setAnswerEn("JUnit is a testing framework");
                newFaq.setCategory("Testing");

                Faq createdFaq = new Faq();
                createdFaq.setId(3L);
                createdFaq.setQuestionEn(newFaq.getQuestionEn());
                createdFaq.setAnswerEn(newFaq.getAnswerEn());
                createdFaq.setCategory(newFaq.getCategory());

                given(faqService.createFaq(any(Faq.class))).willReturn(createdFaq);

                // When & Then
                MvcResult result = mockMvc.perform(post("/api/faq")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newFaq)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                Faq returnedFaq = objectMapper.readValue(responseContent, Faq.class);

                assertThat(returnedFaq)
                                .isNotNull()
                                .satisfies(faq -> {
                                        assertThat(faq.getId()).isEqualTo(3L);
                                        assertThat(faq.getQuestionEn()).isEqualTo("What is JUnit?");
                                        assertThat(faq.getAnswerEn()).isEqualTo("JUnit is a testing framework");
                                        assertThat(faq.getCategory()).isEqualTo("Testing");
                                });
        }

        @Test
        void givenInvalidFaqData_whenCreateFaq_thenReturnBadRequest() throws Exception {
                // Given
                String invalidJson = "{ invalid json }";

                // When & Then
                mockMvc.perform(post("/api/faq")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                                .andExpect(status().isBadRequest());
        }

        // PUT /api/faq/{id} - Update FAQ Tests

        @Test
        void givenExistingFaqIdAndValidData_whenUpdateFaq_thenReturnUpdatedFaq() throws Exception {
                // Given
                Long faqId = 1L;
                Faq updateData = new Faq();
                updateData.setQuestionEn("Updated Question");
                updateData.setAnswerEn("Updated Answer");
                updateData.setCategory("Updated Category");

                Faq updatedFaq = new Faq();
                updatedFaq.setId(faqId);
                updatedFaq.setQuestionEn(updateData.getQuestionEn());
                updatedFaq.setAnswerEn(updateData.getAnswerEn());
                updatedFaq.setCategory(updateData.getCategory());

                given(faqService.updateFaq(eq(faqId), any(Faq.class))).willReturn(updatedFaq);

                // When & Then
                MvcResult result = mockMvc.perform(put("/api/faq/{id}", faqId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andReturn();

                String responseContent = result.getResponse().getContentAsString();
                Faq returnedFaq = objectMapper.readValue(responseContent, Faq.class);

                assertThat(returnedFaq)
                                .isNotNull()
                                .satisfies(faq -> {
                                        assertThat(faq.getId()).isEqualTo(faqId);
                                        assertThat(faq.getQuestionEn()).isEqualTo("Updated Question");
                                        assertThat(faq.getAnswerEn()).isEqualTo("Updated Answer");
                                        assertThat(faq.getCategory()).isEqualTo("Updated Category");
                                });
        }

        @Test
        void givenNonExistingFaqId_whenUpdateFaq_thenThrowResourceNotFoundException() throws Exception {
                // Given
                Long nonExistingId = 999L;
                Faq updateData = new Faq();
                updateData.setQuestionEn("Updated Question");

                given(faqService.updateFaq(eq(nonExistingId), any(Faq.class)))
                                .willThrow(new ResourceNotFoundException("FAQ not found with id " + nonExistingId));

                // When & Then
                mockMvc.perform(put("/api/faq/{id}", nonExistingId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void givenInvalidFaqData_whenUpdateFaq_thenReturnBadRequest() throws Exception {
                // Given
                Long faqId = 1L;
                String invalidJson = "{ invalid json }";

                // When & Then
                mockMvc.perform(put("/api/faq/{id}", faqId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                                .andExpect(status().isBadRequest());
        }

        // DELETE /api/faq/{id} - Delete FAQ Tests

        @Test
        void givenExistingFaqId_whenDeleteFaq_thenReturnNoContent() throws Exception {
                // Given
                Long faqId = 1L;
                willDoNothing().given(faqService).deleteFaq(faqId);

                // When & Then
                mockMvc.perform(delete("/api/faq/{id}", faqId))
                                .andExpect(status().isNoContent())
                                .andExpect(content().string(""));
        }

        @Test
        void givenNonExistingFaqId_whenDeleteFaq_thenThrowResourceNotFoundException() throws Exception {
                // Given
                Long nonExistingId = 999L;
                willThrow(new ResourceNotFoundException("FAQ not found with id " + nonExistingId))
                                .given(faqService).deleteFaq(nonExistingId);

                // When & Then
                mockMvc.perform(delete("/api/faq/{id}", nonExistingId))
                                .andExpect(status().isNotFound());
        }

        // Edge Cases and Additional Tests

        @Test
        void givenNullId_whenGetFaqById_thenReturnBadRequest() throws Exception {
                // When & Then
                mockMvc.perform(get("/api/faq/{id}", "null")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void givenInvalidIdFormat_whenGetFaqById_thenReturnBadRequest() throws Exception {
                // When & Then
                mockMvc.perform(get("/api/faq/{id}", "invalid-id")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void givenEmptyRequestBody_whenCreateFaq_thenReturnBadRequest() throws Exception {
                // When & Then
                mockMvc.perform(post("/api/faq")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                                .andExpect(status().isBadRequest());
        }
}