package periskal.academy.backend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.dto.progress.TrainingProgressDTO;
import periskal.academy.backend.jpa.progress.CompletedChapter;
import periskal.academy.backend.jpa.progress.CompletedChapterRepository;
import periskal.academy.backend.jpa.progress.ModuleProgress;
import periskal.academy.backend.jpa.progress.ModuleProgressRepository;
import periskal.academy.backend.jpa.progress.TrainingProgress;
import periskal.academy.backend.jpa.progress.TrainingProgressRepository;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private TrainingProgressRepository trainingProgressRepository;
    @Mock
    private ModuleProgressRepository moduleProgressRepository;
    @Mock
    private CompletedChapterRepository completedChapterRepository;
    @Mock
    private TrainingService trainingService; 
    @Mock
    private ModuleService moduleService;



    @InjectMocks
    private ProgressService progressService;

    private String testUserId;
    private Long testTrainingId;
    private Long testModuleId;
    private Long testChapterId;

    @BeforeEach
    void setUp() {
        testUserId = "user123";
        testTrainingId = 1L;
        testModuleId = 101L;
        testChapterId = 1001L;
    }

    @Test
    void getUserProgress_shouldReturnUserProgressDTOs() {
        // Arrange
        TrainingProgress progress1 = new TrainingProgress();
        progress1.setId(1L);
        progress1.setUserId(testUserId);
        progress1.setTrainingId(testTrainingId);
        progress1.setOverallPercentComplete(50.0);

        ModuleProgress mp1 = new ModuleProgress();
        mp1.setModuleId(testModuleId);
        mp1.setPercentComplete(50.0);
        CompletedChapter cc1 = new CompletedChapter();
        cc1.setChapterId(testChapterId);
        mp1.getCompletedChapters().add(cc1);
        progress1.getModuleProgress().add(mp1);


        when(trainingProgressRepository.findByUserId(testUserId)).thenReturn(Arrays.asList(progress1));

        // Act
        List<TrainingProgressDTO> result = progressService.getUserProgress(testUserId);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        TrainingProgressDTO dto = result.get(0);
        assertThat(dto.getUserId()).isEqualTo(testUserId);
        assertThat(dto.getTrainingId()).isEqualTo(testTrainingId);
        assertThat(dto.getOverallPercentComplete()).isEqualTo(50.0);
        assertThat(dto.getModuleProgress()).hasSize(1);
        assertThat(dto.getModuleProgress().get(0).getModuleId()).isEqualTo(testModuleId);
        assertThat(dto.getModuleProgress().get(0).getCompletedChapters()).containsExactly(testChapterId);

        verify(trainingProgressRepository).findByUserId(testUserId);
    }

    @Test
    void getUserProgress_whenNoProgress_shouldReturnEmptyList() {
        // Arrange
        when(trainingProgressRepository.findByUserId(testUserId)).thenReturn(Collections.emptyList());

        // Act
        List<TrainingProgressDTO> result = progressService.getUserProgress(testUserId);

        // Assert
        assertThat(result).isNotNull().isEmpty();
        verify(trainingProgressRepository).findByUserId(testUserId);
    }

    @Test
    void getTrainingProgress_shouldReturnTrainingProgressDTO() {
        // Arrange
        TrainingProgress progress = new TrainingProgress();
        progress.setId(1L);
        progress.setUserId(testUserId);
        progress.setTrainingId(testTrainingId);
        progress.setOverallPercentComplete(25.0);

        ModuleProgress mp = new ModuleProgress();
        mp.setModuleId(testModuleId);
        mp.setPercentComplete(25.0);
        progress.getModuleProgress().add(mp);


        when(trainingProgressRepository.findByUserIdAndTrainingId(testUserId, testTrainingId))
                .thenReturn(Optional.of(progress));

        // Act
        TrainingProgressDTO result = progressService.getTrainingProgress(testUserId, testTrainingId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        assertThat(result.getTrainingId()).isEqualTo(testTrainingId);
        assertThat(result.getOverallPercentComplete()).isEqualTo(25.0);
        assertThat(result.getModuleProgress()).hasSize(1);

        verify(trainingProgressRepository).findByUserIdAndTrainingId(testUserId, testTrainingId);
    }

    @Test
    void getTrainingProgress_whenProgressNotFound_shouldReturnNull() {
        // Arrange
        when(trainingProgressRepository.findByUserIdAndTrainingId(testUserId, testTrainingId))
                .thenReturn(Optional.empty());

        // Act
        TrainingProgressDTO result = progressService.getTrainingProgress(testUserId, testTrainingId);

        // Assert
        assertThat(result).isNull();
        verify(trainingProgressRepository).findByUserIdAndTrainingId(testUserId, testTrainingId);
    }
    
    @Test
    void completeChapter_whenChapterAlreadyCompleted_shouldNotUpdateAndReturnExistingProgress() {
        // Arrange
        TrainingProgress existingTrainingProgress = new TrainingProgress();
        existingTrainingProgress.setId(1L);
        existingTrainingProgress.setUserId(testUserId);
        existingTrainingProgress.setTrainingId(testTrainingId);
        existingTrainingProgress.setOverallPercentComplete(100.0); // Already fully complete

        ModuleProgress existingModuleProgress = new ModuleProgress();
        existingModuleProgress.setId(10L);
        existingModuleProgress.setTrainingProgress(existingTrainingProgress);
        existingModuleProgress.setModuleId(testModuleId);
        existingModuleProgress.setPercentComplete(100.0);
        
        CompletedChapter completedChapter = new CompletedChapter();
        completedChapter.setId(100L);
        completedChapter.setChapterId(testChapterId);
        completedChapter.setModuleProgress(existingModuleProgress);
        existingModuleProgress.getCompletedChapters().add(completedChapter);
        
        existingTrainingProgress.getModuleProgress().add(existingModuleProgress);

        when(trainingProgressRepository.findByUserIdAndTrainingId(testUserId, testTrainingId))
                .thenReturn(Optional.of(existingTrainingProgress));

        when(moduleProgressRepository.findByTrainingProgressIdAndModuleId(1L, testModuleId))
                .thenReturn(Optional.of(existingModuleProgress));

        when(completedChapterRepository.existsByModuleProgressIdAndChapterId(10L, testChapterId))
                .thenReturn(true);
        
        
        when(trainingProgressRepository.save(existingTrainingProgress)).thenReturn(existingTrainingProgress);

        // Act
        TrainingProgressDTO result = progressService.completeChapter(testUserId, testTrainingId, testModuleId, testChapterId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOverallPercentComplete()).isEqualTo(100.0); 
        assertThat(result.getModuleProgress().get(0).getPercentComplete()).isEqualTo(100.0); 
        assertThat(result.getModuleProgress().get(0).getCompletedChapters()).containsExactly(testChapterId);

        verify(completedChapterRepository, never()).save(any(CompletedChapter.class));
        verify(moduleService, never()).getModuleById(anyLong()); 
        verify(moduleProgressRepository, never()).save(argThat(mp -> mp.getId() != null && mp.getId().equals(10L) && mp.getPercentComplete() != 100.0)); 
        verify(trainingProgressRepository).save(existingTrainingProgress); 
    }
    
}