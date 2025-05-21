package periskal.academy.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.jpa.CompletedExam;
import periskal.academy.backend.jpa.CompletedExamRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.CompletedExamService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletedExamServiceTest {

    @Mock
    private CompletedExamRepository completedExamRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private CompletedExamService completedExamService;

    // --- Reusable Test Data Instances ---
    private CompletedExamDTO baseCompletedExamDTO;
    private CompletedExam baseCompletedExamEntity;
    private Exam baseExam;
    private Training baseTraining;
    private Date baseTestDate;

    // --- Helper methods for creating test objects ---

    private CompletedExamDTO createCompletedExamDTO(Long id, String userId, Long trainingId, Long examId,
            int score, int requiredScore, Date completedDate,
            String examName, String trainingName) {
        CompletedExamDTO dto = new CompletedExamDTO();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setTrainingId(trainingId);
        dto.setExamId(examId);
        dto.setScore(score);
        dto.setRequiredScore(requiredScore);
        dto.setCompletedDate(completedDate);
        dto.setExamName(examName); // For expected DTOs after conversion
        dto.setTrainingName(trainingName); // For expected DTOs after conversion
        return dto;
    }

    // Overload for DTO before conversion (without examName, trainingName)
    private CompletedExamDTO createCompletedExamDTO(Long id, String userId, Long trainingId, Long examId,
            int score, int requiredScore, Date completedDate) {
        return createCompletedExamDTO(id, userId, trainingId, examId, score, requiredScore, completedDate, null, null);
    }

    private CompletedExam createCompletedExamEntity(Long id, String userId, Long trainingId, Long examId,
            int score, int requiredScore, Date completedDate) {
        CompletedExam entity = new CompletedExam();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setTrainingId(trainingId);
        entity.setExamId(examId);
        entity.setScore(score);
        entity.setRequiredScore(requiredScore);
        entity.setCompletedDate(completedDate);
        return entity;
    }

    private Exam createExamEntity(Long id, String title, String description, int totalQuestions) {
        Exam exam = new Exam(title, description, totalQuestions);
        exam.setId(id);
        return exam;
    }

    private Training createTrainingEntity(Long id, String nameEn, String descriptionEn) {
        Training training = new Training();
        training.setId(id);
        training.setNameEn(nameEn);
        training.setDescriptionEn(descriptionEn);
        // Set other fields if necessary for specific tests
        return training;
    }

    @BeforeEach
    void setUp() {
        baseTestDate = new Date();

        baseExam = createExamEntity(20L, "Java Basics Exam", "Covers fundamental Java concepts", 25);
        baseTraining = createTrainingEntity(10L, "Core Java Training", "Comprehensive training on Core Java.");

        baseCompletedExamDTO = createCompletedExamDTO(1L, "user123", baseTraining.getId(), baseExam.getId(),
                85, 70, baseTestDate);
        baseCompletedExamEntity = createCompletedExamEntity(1L, "user123", baseTraining.getId(), baseExam.getId(),
                85, 70, baseTestDate);
    }

    @Test
    void givenCompletedExamDTO_whenSaveCompletedExam_thenReturnsSavedDTOWithNames() {
        // Given
        Date newDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
        CompletedExamDTO inputDTO = createCompletedExamDTO(null, "userNew", 11L, 21L, 90, 75, newDate); // ID is null
                                                                                                        // for new
                                                                                                        // entity

        CompletedExam savedEntity = createCompletedExamEntity(2L, inputDTO.getUserId(), inputDTO.getTrainingId(),
                inputDTO.getExamId(), inputDTO.getScore(),
                inputDTO.getRequiredScore(), inputDTO.getCompletedDate());

        Exam mockExam = createExamEntity(21L, "Advanced Java", "Deep dive into Java", 30);
        Training mockTraining = createTrainingEntity(11L, "Spring Boot Microservices", "Microservices desc");

        when(completedExamRepository.save(any(CompletedExam.class))).thenReturn(savedEntity);
        when(examRepository.findById(mockExam.getId())).thenReturn(Optional.of(mockExam));
        when(trainingRepository.findById(mockTraining.getId())).thenReturn(Optional.of(mockTraining));

        // When
        CompletedExamDTO resultDTO = completedExamService.saveCompletedExam(inputDTO);

        // Then
        assertThat(resultDTO).isNotNull();
        assertThat(resultDTO.getId()).isEqualTo(savedEntity.getId());
        assertThat(resultDTO.getUserId()).isEqualTo(inputDTO.getUserId());
        assertThat(resultDTO.getTrainingId()).isEqualTo(inputDTO.getTrainingId());
        assertThat(resultDTO.getExamId()).isEqualTo(inputDTO.getExamId());
        assertThat(resultDTO.getScore()).isEqualTo(inputDTO.getScore());
        assertThat(resultDTO.getCompletedDate()).isEqualTo(inputDTO.getCompletedDate());
        assertThat(resultDTO.getExamName()).isEqualTo(mockExam.getExamTitle());
        assertThat(resultDTO.getTrainingName()).isEqualTo(mockTraining.getNameEn());

        ArgumentCaptor<CompletedExam> entityCaptor = ArgumentCaptor.forClass(CompletedExam.class);
        verify(completedExamRepository).save(entityCaptor.capture());
        CompletedExam capturedEntity = entityCaptor.getValue();

        // Asserting the entity passed to save method (before ID is assigned)
        assertThat(capturedEntity.getId()).isNull(); // Or matches inputDTO.getId() if it was an update
        assertThat(capturedEntity.getUserId()).isEqualTo(inputDTO.getUserId());
        // ... other fields

        verify(examRepository).findById(mockExam.getId());
        verify(trainingRepository).findById(mockTraining.getId());
    }

    @Test
    void givenCompletedExamDTOWithNonExistentExam_whenSaveCompletedExam_thenReturnsDTOWithExamNameNA() {
        // Given
        Date testDate = new Date();
        CompletedExamDTO inputDTO = createCompletedExamDTO(null, "userTest", 15L, 25L, 80, 70, testDate);

        CompletedExam savedEntity = createCompletedExamEntity(3L, inputDTO.getUserId(), inputDTO.getTrainingId(),
                inputDTO.getExamId(), inputDTO.getScore(),
                inputDTO.getRequiredScore(), inputDTO.getCompletedDate());

        Training mockTraining = createTrainingEntity(15L, "Agile Methodology", "Agile desc");

        when(completedExamRepository.save(any(CompletedExam.class))).thenReturn(savedEntity);
        when(examRepository.findById(25L)).thenReturn(Optional.empty()); // Exam not found
        when(trainingRepository.findById(mockTraining.getId())).thenReturn(Optional.of(mockTraining));

        // When
        CompletedExamDTO resultDTO = completedExamService.saveCompletedExam(inputDTO);

        // Then
        assertThat(resultDTO).isNotNull();
        assertThat(resultDTO.getId()).isEqualTo(savedEntity.getId());
        assertThat(resultDTO.getExamName()).isEqualTo("N/A");
        assertThat(resultDTO.getTrainingName()).isEqualTo(mockTraining.getNameEn());

        verify(examRepository).findById(25L);
        verify(trainingRepository).findById(mockTraining.getId());
    }

    @Test
    void givenUserIdWithCompletedExams_whenGetCompletedExamsByUserId_thenReturnsListOfDTOs() {
        // Given
        String userId = "user123";
        Date date1 = new Date();
        Date date2 = new Date(System.currentTimeMillis() - 100000);

        Exam examDetails1 = createExamEntity(20L, "Java Basics", "Desc1", 10);
        Training trainingDetails1 = createTrainingEntity(10L, "Core Java", "Desc Core Java");
        Exam examDetails2 = createExamEntity(21L, "Spring Core", "Desc2", 15);
        Training trainingDetails2 = createTrainingEntity(11L, "Spring Framework", "Desc Spring");

        CompletedExam entity1 = createCompletedExamEntity(1L, userId, trainingDetails1.getId(), examDetails1.getId(),
                80, 70, date1);
        CompletedExam entity2 = createCompletedExamEntity(2L, userId, trainingDetails2.getId(), examDetails2.getId(),
                90, 75, date2);
        List<CompletedExam> mockEntities = List.of(entity1, entity2);

        when(completedExamRepository.findByUserId(userId)).thenReturn(mockEntities);
        when(examRepository.findById(examDetails1.getId())).thenReturn(Optional.of(examDetails1));
        when(trainingRepository.findById(trainingDetails1.getId())).thenReturn(Optional.of(trainingDetails1));
        when(examRepository.findById(examDetails2.getId())).thenReturn(Optional.of(examDetails2));
        when(trainingRepository.findById(trainingDetails2.getId())).thenReturn(Optional.of(trainingDetails2));

        // When
        List<CompletedExamDTO> resultDTOs = completedExamService.getCompletedExamsByUserId(userId);

        // Then
        assertThat(resultDTOs).isNotNull().hasSize(2);

        assertThat(resultDTOs.get(0).getId()).isEqualTo(entity1.getId());
        assertThat(resultDTOs.get(0).getExamName()).isEqualTo(examDetails1.getExamTitle());
        assertThat(resultDTOs.get(0).getTrainingName()).isEqualTo(trainingDetails1.getNameEn());

        assertThat(resultDTOs.get(1).getId()).isEqualTo(entity2.getId());
        assertThat(resultDTOs.get(1).getExamName()).isEqualTo(examDetails2.getExamTitle());
        assertThat(resultDTOs.get(1).getTrainingName()).isEqualTo(trainingDetails2.getNameEn());

        verify(completedExamRepository).findByUserId(userId);
        verify(examRepository, times(1)).findById(examDetails1.getId());
        verify(examRepository, times(1)).findById(examDetails2.getId());
        verify(trainingRepository, times(1)).findById(trainingDetails1.getId());
        verify(trainingRepository, times(1)).findById(trainingDetails2.getId());
    }

    @Test
    void givenUserIdWithNoCompletedExams_whenGetCompletedExamsByUserId_thenReturnsEmptyList() {
        // Given
        String userId = "user456";
        when(completedExamRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<CompletedExamDTO> resultDTOs = completedExamService.getCompletedExamsByUserId(userId);

        // Then
        assertThat(resultDTOs).isNotNull().isEmpty();
        verify(completedExamRepository).findByUserId(userId);
        verifyNoInteractions(examRepository, trainingRepository); // More concise
    }

    @Test
    void givenUserIdWithCompletedExamButMissingTrainingDetails_whenGetCompletedExamsByUserId_thenReturnsDTOWithTrainingNameNA() {
        // Given
        String userId = "user789";
        Exam examDetails = createExamEntity(50L, "Some Exam", "Desc", 10);
        // Training details will be missing for trainingId 60L
        CompletedExam examEntity = createCompletedExamEntity(5L, userId, 60L, examDetails.getId(), 88, 70, new Date());

        when(completedExamRepository.findByUserId(userId)).thenReturn(List.of(examEntity));
        when(examRepository.findById(examDetails.getId())).thenReturn(Optional.of(examDetails));
        when(trainingRepository.findById(60L)).thenReturn(Optional.empty()); // Training details not found

        // When
        List<CompletedExamDTO> resultDTOs = completedExamService.getCompletedExamsByUserId(userId);

        // Then
        assertThat(resultDTOs).isNotNull().hasSize(1);
        assertThat(resultDTOs.get(0).getExamName()).isEqualTo(examDetails.getExamTitle());
        assertThat(resultDTOs.get(0).getTrainingName()).isEqualTo("N/A");

        verify(trainingRepository).findById(60L);
    }

    @Test
    void givenUserIdAndTrainingIdWithCompletedExams_whenGetCompletedExamsByUserIdAndTrainingId_thenReturnsListOfDTOs() {
        // Given
        String userId = baseCompletedExamEntity.getUserId(); // "user123"
        Long trainingIdParam = baseTraining.getId(); // 10L

        List<CompletedExam> mockEntities = List.of(baseCompletedExamEntity);

        when(completedExamRepository.findByUserIdAndTrainingId(userId, trainingIdParam)).thenReturn(mockEntities);
        when(examRepository.findById(baseExam.getId())).thenReturn(Optional.of(baseExam));
        when(trainingRepository.findById(baseTraining.getId())).thenReturn(Optional.of(baseTraining));

        // When
        List<CompletedExamDTO> resultDTOs = completedExamService.getCompletedExamsByUserIdAndTrainingId(userId,
                trainingIdParam);

        // Then
        assertThat(resultDTOs).isNotNull().hasSize(1);
        CompletedExamDTO resultDTO = resultDTOs.get(0);

        assertThat(resultDTO.getId()).isEqualTo(baseCompletedExamEntity.getId());
        assertThat(resultDTO.getUserId()).isEqualTo(baseCompletedExamEntity.getUserId());
        assertThat(resultDTO.getTrainingId()).isEqualTo(baseCompletedExamEntity.getTrainingId());
        assertThat(resultDTO.getExamId()).isEqualTo(baseCompletedExamEntity.getExamId());
        assertThat(resultDTO.getScore()).isEqualTo(baseCompletedExamEntity.getScore());
        assertThat(resultDTO.getCompletedDate()).isEqualTo(baseCompletedExamEntity.getCompletedDate());
        assertThat(resultDTO.getExamName()).isEqualTo(baseExam.getExamTitle());
        assertThat(resultDTO.getTrainingName()).isEqualTo(baseTraining.getNameEn());

        verify(completedExamRepository).findByUserIdAndTrainingId(userId, trainingIdParam);
        verify(examRepository).findById(baseExam.getId());
        verify(trainingRepository).findById(baseTraining.getId());
    }

    @Test
    void givenUserIdAndTrainingIdWithNoCompletedExams_whenGetCompletedExamsByUserIdAndTrainingId_thenReturnsEmptyList() {
        // Given
        String userId = "user789";
        Long trainingId = 30L;
        when(completedExamRepository.findByUserIdAndTrainingId(userId, trainingId)).thenReturn(Collections.emptyList());

        // When
        List<CompletedExamDTO> resultDTOs = completedExamService.getCompletedExamsByUserIdAndTrainingId(userId,
                trainingId);

        // Then
        assertThat(resultDTOs).isNotNull().isEmpty();
        verify(completedExamRepository).findByUserIdAndTrainingId(userId, trainingId);
        verifyNoInteractions(examRepository, trainingRepository);
    }
}