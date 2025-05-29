package periskal.academy.backend.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import periskal.academy.backend.jpa.assignment.TrainingAssignment;
import periskal.academy.backend.service.TrainingAssignmentService;
import periskal.academy.backend.util.JwtUtil;

@WebMvcTest(controllers = TrainingAssignmentController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class })
class TrainingAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingAssignmentService assignmentService;

    private TrainingAssignment assignment1;
    private TrainingAssignment assignment2;
    private List<TrainingAssignment> assignments;

    @BeforeEach
    void setUp() {
        assignment1 = new TrainingAssignment();
        assignment1.setId(1L);
        assignment1.setSkipperId("skipper1");
        assignment1.setTrainingId(101L);
        assignment1.setAssignedDate(new Date());
        assignment1.setAssignedBy("manager1");

        assignment2 = new TrainingAssignment();
        assignment2.setId(2L);
        assignment2.setSkipperId("skipper2");
        assignment2.setTrainingId(102L);
        assignment2.setAssignedDate(new Date());
        assignment2.setAssignedBy("manager1");

        assignments = Arrays.asList(assignment1, assignment2);
    }

    @Test
    void whenCreateAssignments_thenReturnCreatedAssignments() throws Exception {
        // Arrange
        when(assignmentService.createOrUpdateAssignments(any())).thenReturn(assignments);

        // Act
        MvcResult result = mockMvc.perform(post("/api/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignments)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        TrainingAssignment[] returnedAssignments = objectMapper.readValue(content, TrainingAssignment[].class);
        
        assertThat(returnedAssignments).hasSize(2);
        assertThat(returnedAssignments[0].getSkipperId()).isEqualTo("skipper1");
        assertThat(returnedAssignments[1].getSkipperId()).isEqualTo("skipper2");
    }

    @Test
    void whenGetAssignmentsForSkipper_thenReturnAssignmentsList() throws Exception {
        // Arrange
        String skipperId = "skipper1";
        List<TrainingAssignment> skipperAssignments = Arrays.asList(assignment1);
        when(assignmentService.getAssignmentsForSkipper(skipperId)).thenReturn(skipperAssignments);

        // Act
        MvcResult result = mockMvc.perform(get("/api/assignments/{skipperId}", skipperId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        TrainingAssignment[] returnedAssignments = objectMapper.readValue(content, TrainingAssignment[].class);
        
        assertThat(returnedAssignments).hasSize(1);
        assertThat(returnedAssignments[0].getSkipperId()).isEqualTo("skipper1");
    }

    @Test
    void whenGetAssignmentsByFleetManager_thenReturnAssignmentsList() throws Exception {
        // Arrange
        String fleetManagerId = "manager1";
        when(assignmentService.getAssignmentsByFleetManager(fleetManagerId)).thenReturn(assignments);

        // Act
        MvcResult result = mockMvc.perform(get("/api/assignments/fleetmanager/{fleetManagerId}", fleetManagerId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        TrainingAssignment[] returnedAssignments = objectMapper.readValue(content, TrainingAssignment[].class);
        
        assertThat(returnedAssignments).hasSize(2);
        assertThat(returnedAssignments[0].getAssignedBy()).isEqualTo("manager1");
        assertThat(returnedAssignments[1].getAssignedBy()).isEqualTo("manager1");
    }

    @Test
    void whenCheckExistingAssignment_thenReturnTrue() throws Exception {
        // Arrange
        String skipperId = "skipper1";
        Long trainingId = 101L;
        when(assignmentService.checkAssignment(skipperId, trainingId)).thenReturn(true);

        // Act
        MvcResult result = mockMvc.perform(get("/api/assignments/{skipperId}/{trainingId}", skipperId, trainingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        Boolean exists = objectMapper.readValue(content, Boolean.class);
        
        assertThat(exists).isTrue();
    }

    @Test
    void whenCheckNonExistingAssignment_thenReturnFalse() throws Exception {
        // Arrange
        String skipperId = "skipper3";
        Long trainingId = 103L;
        when(assignmentService.checkAssignment(skipperId, trainingId)).thenReturn(false);

        // Act
        MvcResult result = mockMvc.perform(get("/api/assignments/{skipperId}/{trainingId}", skipperId, trainingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        Boolean exists = objectMapper.readValue(content, Boolean.class);
        
        assertThat(exists).isFalse();
    }

    @Test
    void whenDeleteAssignment_thenReturnOk() throws Exception {
        // Arrange
        String skipperId = "skipper1";
        Long trainingId = 101L;
        doNothing().when(assignmentService).deleteAssignment(anyString(), anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/assignments/{skipperId}/{trainingId}", skipperId, trainingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
