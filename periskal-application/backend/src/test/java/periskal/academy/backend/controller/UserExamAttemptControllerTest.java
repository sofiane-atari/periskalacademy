package periskal.academy.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import periskal.academy.backend.service.UserExamAttemptService;
import periskal.academy.backend.jpa.TrainingRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserExamAttemptController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
public class UserExamAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserExamAttemptService userExamAttemptService;

    @MockitoBean
    private TrainingRepository trainingRepository;

    private String testUserId;
    private String testExamId;

    @BeforeEach
    void setUp() {
        testUserId = "user123";
        testExamId = "exam456";
    }

    @Test
    void givenUserHasAttempts_whenStartExamIsCalled_thenReturnsOkAndSuccessMessage() throws Exception {
        when(userExamAttemptService.consumeAttempt(testUserId, testExamId)).thenReturn(true);

        mockMvc.perform(post("/api/attempts/{userId}/{examId}/start", testUserId, testExamId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exam started."));
    }

    @Test
    void givenUserHasNoAttempts_whenStartExamIsCalled_thenReturnsBadRequestAndErrorMessage() throws Exception {
        when(userExamAttemptService.consumeAttempt(testUserId, testExamId)).thenReturn(false);

        mockMvc.perform(post("/api/attempts/{userId}/{examId}/start", testUserId, testExamId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No remaining attempts."));
    }
}