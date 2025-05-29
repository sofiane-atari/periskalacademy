package periskal.academy.backend.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import periskal.academy.backend.jpa.assignment.TrainingAssignment;
import periskal.academy.backend.jpa.assignment.TrainingAssignmentRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

@ExtendWith(MockitoExtension.class)
class TrainingAssignmentServiceTest {

    @Mock
    private TrainingAssignmentRepository assignmentRepository;

    @InjectMocks
    private TrainingAssignmentService assignmentService;

    private TrainingAssignment assignment1;
    private TrainingAssignment assignment2;

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
    }

    @Test
    void whenCreateNewAssignments_thenSaveAll() {
        // Arrange
        List<TrainingAssignment> assignments = Arrays.asList(assignment1, assignment2);
        when(assignmentRepository.findBySkipperIdAndTrainingId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(assignmentRepository.saveAll(any())).thenReturn(assignments);

        // Act
        List<TrainingAssignment> result = assignmentService.createOrUpdateAssignments(assignments);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSkipperId()).isEqualTo("skipper1");
        assertThat(result.get(1).getSkipperId()).isEqualTo("skipper2");
        verify(assignmentRepository, times(2)).findBySkipperIdAndTrainingId(anyString(), anyLong());
        verify(assignmentRepository).saveAll(any());
    }

    @Test
    void whenUpdateExistingAssignment_thenUpdateFields() {
        // Arrange
        List<TrainingAssignment> assignments = Arrays.asList(assignment1);
        when(assignmentRepository.findBySkipperIdAndTrainingId("skipper1", 101L)).thenReturn(Optional.of(assignment1));
        when(assignmentRepository.saveAll(any())).thenReturn(assignments);

        // Act
        List<TrainingAssignment> result = assignmentService.createOrUpdateAssignments(assignments);

        // Assert
        assertThat(result).hasSize(1);
        verify(assignmentRepository).findBySkipperIdAndTrainingId("skipper1", 101L);
        verify(assignmentRepository).saveAll(any());
    }

    @Test
    void whenDataIntegrityViolation_thenThrowValidationException() {
        // Arrange
        List<TrainingAssignment> assignments = Arrays.asList(assignment1);
        when(assignmentRepository.findBySkipperIdAndTrainingId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(assignmentRepository.saveAll(any())).thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.createOrUpdateAssignments(assignments))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Failed to create or update assignments");
    }

    @Test
    void whenGetAssignmentsForSkipper_thenReturnAssignmentsList() {
        // Arrange
        List<TrainingAssignment> assignments = Arrays.asList(assignment1);
        when(assignmentRepository.findBySkipperId("skipper1")).thenReturn(assignments);

        // Act
        List<TrainingAssignment> result = assignmentService.getAssignmentsForSkipper("skipper1");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSkipperId()).isEqualTo("skipper1");
        verify(assignmentRepository).findBySkipperId("skipper1");
    }

    @Test
    void whenGetAssignmentsWithNullSkipperId_thenThrowValidationException() {
        // Act & Assert
        assertThatThrownBy(() -> assignmentService.getAssignmentsForSkipper(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Skipper ID cannot be null or empty");
    }

    @Test
    void whenGetAssignmentsByFleetManager_thenReturnAssignmentsList() {
        // Arrange
        List<TrainingAssignment> assignments = Arrays.asList(assignment1, assignment2);
        when(assignmentRepository.findByAssignedBy("manager1")).thenReturn(assignments);

        // Act
        List<TrainingAssignment> result = assignmentService.getAssignmentsByFleetManager("manager1");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAssignedBy()).isEqualTo("manager1");
        assertThat(result.get(1).getAssignedBy()).isEqualTo("manager1");
        verify(assignmentRepository).findByAssignedBy("manager1");
    }

    @Test
    void whenCheckExistingAssignment_thenReturnTrue() {
        // Arrange
        when(assignmentRepository.findBySkipperIdAndTrainingId("skipper1", 101L)).thenReturn(Optional.of(assignment1));

        // Act
        boolean result = assignmentService.checkAssignment("skipper1", 101L);

        // Assert
        assertThat(result).isTrue();
        verify(assignmentRepository).findBySkipperIdAndTrainingId("skipper1", 101L);
    }

    @Test
    void whenCheckNonExistingAssignment_thenReturnFalse() {
        // Arrange
        when(assignmentRepository.findBySkipperIdAndTrainingId("skipper3", 103L)).thenReturn(Optional.empty());

        // Act
        boolean result = assignmentService.checkAssignment("skipper3", 103L);

        // Assert
        assertThat(result).isFalse();
        verify(assignmentRepository).findBySkipperIdAndTrainingId("skipper3", 103L);
    }

    @Test
    void whenDeleteAssignmentByIds_thenDeleteFromRepository() {
        // Arrange
        when(assignmentRepository.findBySkipperIdAndTrainingId("skipper1", 101L)).thenReturn(Optional.of(assignment1));

        // Act
        assignmentService.deleteAssignment("skipper1", 101L);

        // Assert
        verify(assignmentRepository).findBySkipperIdAndTrainingId("skipper1", 101L);
        verify(assignmentRepository).delete(assignment1);
    }

    @Test
    void whenDeleteNonExistingAssignment_thenThrowNotFoundException() {
        // Arrange
        when(assignmentRepository.findBySkipperIdAndTrainingId("skipper3", 103L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.deleteAssignment("skipper3", 103L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Assignment not found");
    }
}
