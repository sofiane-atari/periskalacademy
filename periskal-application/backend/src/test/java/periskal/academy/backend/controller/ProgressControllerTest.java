package periskal.academy.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.dto.progress.ChapterCompletion;
import periskal.academy.backend.dto.progress.ModuleProgressDTO;
import periskal.academy.backend.dto.progress.TrainingProgressDTO;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.service.ProgressService;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) 
class ProgressControllerTest {

    @Mock 
    private ProgressService progressService;

    @InjectMocks 
    private ProgressController progressController;


    private ChapterCompletion chapterCompletionRequest;
    private TrainingProgressDTO sampleTrainingProgressDTO; 
    private String testUserId;
    private Long testTrainingId;
    private Long testModuleId;
    private Long testChapterId;

    @BeforeEach
    void setUp() {
        // Initialize common test data
        testUserId = "Per518";
        testTrainingId = 1L;
        testModuleId = 10L;
        testChapterId = 100L;

        // Setup for the request object sent to the completeChapter endpoint
        chapterCompletionRequest = new ChapterCompletion();
        chapterCompletionRequest.setUserId(testUserId);
        chapterCompletionRequest.setTrainingId(testTrainingId);
        chapterCompletionRequest.setModuleId(testModuleId);
        chapterCompletionRequest.setChapterId(testChapterId); 

        // sample TrainingProgressDTO 
        sampleTrainingProgressDTO = new TrainingProgressDTO();
        sampleTrainingProgressDTO.setId(123L);
        sampleTrainingProgressDTO.setUserId(testUserId);
        sampleTrainingProgressDTO.setTrainingId(testTrainingId);
        sampleTrainingProgressDTO.setOverallPercentComplete(75.0);
        sampleTrainingProgressDTO.setLastUpdated(new Date());
        sampleTrainingProgressDTO.setModuleProgress(new ArrayList<ModuleProgressDTO>());
    }

    @Test
    void whenCompleteChapter_thenReturnOkWithProgress() {
        
        // Arrange
        when(progressService.completeChapter(testUserId, testTrainingId, testModuleId, testChapterId))
                .thenReturn(sampleTrainingProgressDTO);

        // Act
        ResponseEntity<TrainingProgressDTO> response = progressController.completeChapter(chapterCompletionRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 
        assertThat(response.getBody()).isNotNull(); 
        assertThat(response.getBody()).isEqualTo(sampleTrainingProgressDTO);
        assertThat(response.getBody().getUserId()).isEqualTo(testUserId);
        assertThat(response.getBody().getTrainingId()).isEqualTo(testTrainingId);
        assertThat(response.getBody().getOverallPercentComplete()).isEqualTo(75.0);

        verify(progressService).completeChapter(testUserId, testTrainingId, testModuleId, testChapterId);
    }

    @Test
    void whenGetUserProgress_thenReturnOkWithProgressList() {
        
        // Arrange
        List<TrainingProgressDTO> expectedProgressList = Collections.singletonList(sampleTrainingProgressDTO);
        when(progressService.getUserProgress(testUserId)).thenReturn(expectedProgressList);

        // Act
        ResponseEntity<List<TrainingProgressDTO>> response = progressController.getUserProgress(testUserId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1); 
        assertThat(response.getBody().get(0)).isEqualTo(sampleTrainingProgressDTO); 
        assertThat(response.getBody().get(0).getUserId()).isEqualTo(testUserId);

        // Verify 
        verify(progressService).getUserProgress(testUserId);
    }

    @Test
    void whenGetTrainingProgress_withValidIds_thenReturnOkWithProgress() {
        
        // Arrange
        when(progressService.getTrainingProgress(testUserId, testTrainingId)).thenReturn(sampleTrainingProgressDTO);

        // Act
        ResponseEntity<TrainingProgressDTO> response = progressController.getTrainingProgress(testTrainingId, testUserId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(sampleTrainingProgressDTO);
        assertThat(response.getBody().getUserId()).isEqualTo(testUserId);
        assertThat(response.getBody().getTrainingId()).isEqualTo(testTrainingId);

        // Verify 
        verify(progressService).getTrainingProgress(testUserId, testTrainingId);
    }

    @Test
    void whenGetTrainingProgress_withNonExistentProgress_thenThrowsResourceNotFoundException() {
        
        // Arrange
        Long nonExistentTrainingId = 999L;

        when(progressService.getTrainingProgress(testUserId, nonExistentTrainingId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            progressController.getTrainingProgress(nonExistentTrainingId, testUserId);
        });
        
        // Assert exception message
        assertThat(exception.getMessage()).contains("Progress not found for user " + testUserId);
        assertThat(exception.getMessage()).contains("training " + nonExistentTrainingId);

        // Verify 
        verify(progressService).getTrainingProgress(testUserId, nonExistentTrainingId);
    }
}
