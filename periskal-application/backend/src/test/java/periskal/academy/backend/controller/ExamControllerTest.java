package periskal.academy.backend.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.dto.ExamRequest;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.service.ExamService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ExamControllerTest {

    @Mock
    private ExamService examService;

    @InjectMocks
    private ExamController examController;

    private Exam exam;
    private ExamRequest examRequest;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        exam.setId(1L);
        exam.setExamTitle("Test Exam");

        examRequest = new ExamRequest();
        examRequest.setExamTitle("Test Exam");
        // Add other fields as necessary
    }

    @Test
    void givenExamExists_whenGetExamById_thenReturnsExam() {
        when(examService.findById(1L)).thenReturn(Optional.of(exam));

        ResponseEntity<Exam> response = examController.getExamById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(exam);
    }

    @Test
    void givenNoExamExists_whenGetExamById_thenThrowsResourceNotFoundException() {
        when(examService.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            examController.getExamById(1L);
        });
        
        assertThat(exception.getMessage()).contains("Exam not found with id: 1");
    }

    @Test
    void givenExamForTrainingExists_whenGetExamByTrainingId_thenReturnsExam() {
        when(examService.findByTrainingId(1L)).thenReturn(Optional.of(exam));

        ResponseEntity<Exam> response = examController.getExamByTrainingId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(exam);
    }

    @Test
    void givenNoExamForTraining_whenGetExamByTrainingId_thenThrowsResourceNotFoundException() {
        when(examService.findByTrainingId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            examController.getExamByTrainingId(1L);
        });
        
        assertThat(exception.getMessage()).contains("Exam not found for training id: 1");
    }

    @Test
    void givenValidExam_whenUpdateExam_thenReturnsUpdatedExam() {
        Exam updatedExam = new Exam();
        updatedExam.setId(1L);
        updatedExam.setExamTitle("Updated Exam");

        when(examService.updateExam(1L, updatedExam)).thenReturn(updatedExam);

        ResponseEntity<Exam> response = examController.updateExam(1L, updatedExam);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getExamTitle()).isEqualTo("Updated Exam");
    }

    @Test
    void givenNullReturned_whenUpdateExam_thenThrowsResourceNotFoundException() {
        when(examService.updateExam(eq(1L), any(Exam.class))).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            examController.updateExam(1L, exam);
        });
        
        assertThat(exception.getMessage()).contains("Exam not found with id: 1");
    }

    @Test
    void givenValidExamRequest_whenCreateExam_thenReturnsCreatedExam() {
        when(examService.createExam(examRequest)).thenReturn(exam);

        ResponseEntity<Exam> response = examController.createExamWithQuestions(examRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(exam);
    }

    @Test
    void givenValidId_whenDeleteExam_thenReturnsSuccessMessage() {
        doNothing().when(examService).deleteExam(1L);

        ResponseEntity<Map<String, String>> response = examController.deleteExam(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "Exam deleted successfully");
        verify(examService, times(1)).deleteExam(1L);
    }
}
