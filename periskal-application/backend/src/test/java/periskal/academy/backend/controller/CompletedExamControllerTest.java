package periskal.academy.backend.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.service.CompletedExamService;
import periskal.academy.backend.util.JwtUtil;

@WebMvcTest(controllers = CompletedExamController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class}) 
class CompletedExamControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CompletedExamService completedExamService;

    @Autowired
    private ObjectMapper objectMapper; 

    private CompletedExamDTO completedExamDTO1;
    private CompletedExamDTO completedExamDTO2;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();

        completedExamDTO1 = new CompletedExamDTO();
        completedExamDTO1.setId(1L);
        completedExamDTO1.setUserId("user123");
        completedExamDTO1.setTrainingId(10L);
        completedExamDTO1.setExamId(20L);
        completedExamDTO1.setRequiredScore(70);
        completedExamDTO1.setScore(85);
        completedExamDTO1.setCompletedDate(testDate);
        completedExamDTO1.setExamName("Java Basics Exam");
        completedExamDTO1.setTrainingName("Core Java Training");

        completedExamDTO2 = new CompletedExamDTO();
        completedExamDTO2.setId(2L);
        completedExamDTO2.setUserId("user123");
        completedExamDTO2.setTrainingId(11L);
        completedExamDTO2.setExamId(21L);
        completedExamDTO2.setRequiredScore(75);
        completedExamDTO2.setScore(90);
        completedExamDTO2.setCompletedDate(new Date(testDate.getTime() - 100000)); 
        completedExamDTO2.setExamName("Spring Boot Fundamentals");
        completedExamDTO2.setTrainingName("Spring Boot Training");
    }

    @Test
    void givenUserIdWithCompletedExams_whenGetUserCompletedExams_thenReturnsOkWithListOfExams() throws Exception {
        // Arrange
        String userId = "user123";
        List<CompletedExamDTO> expectedExams = Arrays.asList(completedExamDTO1, completedExamDTO2);
        given(completedExamService.getCompletedExamsByUserId(userId)).willReturn(expectedExams);

        // Act
        ResultActions response = mockMvc.perform(get("/api/completed-exams/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(completedExamDTO1.getId().intValue())))
                .andExpect(jsonPath("$[0].userId", is(completedExamDTO1.getUserId())))
                .andExpect(jsonPath("$[0].examName", is(completedExamDTO1.getExamName())))
                .andExpect(jsonPath("$[1].id", is(completedExamDTO2.getId().intValue())))
                .andExpect(jsonPath("$[1].userId", is(completedExamDTO2.getUserId())))
                .andExpect(jsonPath("$[1].examName", is(completedExamDTO2.getExamName())));

        verify(completedExamService, times(1)).getCompletedExamsByUserId(userId);
    }

    @Test
    void givenUserIdWithNoCompletedExams_whenGetUserCompletedExams_thenReturnsOkWithEmptyList() throws Exception {
        // Arrange
        String userId = "user-no-exams";
        given(completedExamService.getCompletedExamsByUserId(userId)).willReturn(Collections.emptyList());

        // Act
        ResultActions response = mockMvc.perform(get("/api/completed-exams/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(completedExamService, times(1)).getCompletedExamsByUserId(userId);
    }

    @Test
    void givenCompletedExamDTO_whenSaveCompletedExam_thenReturnsOkWithSavedExamDTO() throws Exception {
        // Arrange
        CompletedExamDTO inputDTO = new CompletedExamDTO(); 
        inputDTO.setUserId("newUser");
        inputDTO.setTrainingId(30L);
        inputDTO.setExamId(40L);
        inputDTO.setScore(95);
        inputDTO.setRequiredScore(80);
        inputDTO.setCompletedDate(new Date());

        CompletedExamDTO savedDTO = new CompletedExamDTO(); 
        savedDTO.setId(3L); 
        savedDTO.setUserId(inputDTO.getUserId());
        savedDTO.setTrainingId(inputDTO.getTrainingId());
        savedDTO.setExamId(inputDTO.getExamId());
        savedDTO.setScore(inputDTO.getScore());
        savedDTO.setRequiredScore(inputDTO.getRequiredScore());
        savedDTO.setCompletedDate(inputDTO.getCompletedDate());
        savedDTO.setExamName("New Mock Exam");
        savedDTO.setTrainingName("New Mock Training");

        given(completedExamService.saveCompletedExam(any(CompletedExamDTO.class))).willReturn(savedDTO);

        // Act
        ResultActions response = mockMvc.perform(post("/api/completed-exams")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO))); 

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(savedDTO.getUserId())))
                .andExpect(jsonPath("$.examId", is(savedDTO.getExamId().intValue())))
                .andExpect(jsonPath("$.score", is(savedDTO.getScore())))
                .andExpect(jsonPath("$.examName", is(savedDTO.getExamName())))
                .andExpect(jsonPath("$.trainingName", is(savedDTO.getTrainingName())));

        verify(completedExamService, times(1)).saveCompletedExam(any(CompletedExamDTO.class));
    }

    
}