package periskal.academy.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import periskal.academy.backend.dto.ContactRequest;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.EmailService;
import periskal.academy.backend.util.JwtUtil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private TrainingRepository trainingRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "SKIPPER")
    void givenValidContactRequest_whenSubmitContactForm_thenReturnsOkResponse() throws Exception {
        
        // Given
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName("John");
        contactRequest.setLastName("Doe");
        contactRequest.setEmail("john@example.com");
        contactRequest.setMessage("Test message");

        // When & Then
        mockMvc.perform(post("/api/contact")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.message").value("Test message"));

           
            verify(emailService).sendContactFormEmail(any(ContactRequest.class));
        }
    }
