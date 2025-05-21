package periskal.academy.backend.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.dto.CertificateDTO;
import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.jpa.CompletedExam;
import periskal.academy.backend.jpa.CompletedExamRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.CertificateService;
import periskal.academy.backend.service.CompletedExamService;

@ExtendWith(MockitoExtension.class)
class CompletedExamServiceTest {

    @Mock
    private CompletedExamRepository completedExamRepository;
    
    @Mock
    private ExamRepository examRepository;
    
    @Mock
    private TrainingRepository trainingRepository;
    
    @Mock
    private CertificateService certificateService;
    
    @InjectMocks
    private CompletedExamService completedExamService;
    
    private CompletedExamDTO passedExamDTO;
    private CompletedExamDTO failedExamDTO;
    private CompletedExam completedExam;
    private Training training;
    private Exam exam;
    
    @BeforeEach
    void setUp() {
        // Set up passed exam DTO
        passedExamDTO = new CompletedExamDTO();
        passedExamDTO.setId(1L);
        passedExamDTO.setUserId("user123");
        passedExamDTO.setTrainingId(10L);
        passedExamDTO.setExamId(5L);
        passedExamDTO.setRequiredScore(70);
        passedExamDTO.setScore(85); // Pass
        passedExamDTO.setCompletedDate(new Date());
        
        // Set up failed exam DTO
        failedExamDTO = new CompletedExamDTO();
        failedExamDTO.setId(2L);
        failedExamDTO.setUserId("user456");
        failedExamDTO.setTrainingId(10L);
        failedExamDTO.setExamId(5L);
        failedExamDTO.setRequiredScore(70);
        failedExamDTO.setScore(60); // Fail
        failedExamDTO.setCompletedDate(new Date());
        
        // Set up completed exam entity
        completedExam = new CompletedExam();
        completedExam.setId(1L);
        completedExam.setUserId("user123");
        completedExam.setTrainingId(10L);
        completedExam.setExamId(5L);
        completedExam.setRequiredScore(70);
        completedExam.setScore(85);
        completedExam.setCompletedDate(new Date());
        
        // Set up training
        training = new Training();
        training.setId(10L);
        training.setNameEn("Safety Training");
        
        // Set up exam
        exam = new Exam();
        exam.setId(5L);
        exam.setExamTitle("Safety Exam");
    }
    
    @Test
    void whenSavePassedExam_thenCreatesCertificate() {
        // Arrange
        when(completedExamRepository.save(any(CompletedExam.class))).thenReturn(completedExam);
        when(examRepository.findById(5L)).thenReturn(Optional.of(exam));
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));
        
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setId(1L);
        certificateDTO.setName("Safety Training Certificate");
        certificateDTO.setIssueDate(LocalDate.now());
        certificateDTO.setExpiryDate(LocalDate.now().plusYears(5));
        certificateDTO.setTraining("Safety Training");
        certificateDTO.setReadyForDownload(true);
        certificateDTO.setUserId("user123");
        
        when(certificateService.saveCertificate(any(CertificateDTO.class))).thenReturn(certificateDTO);
        
        // Act
        CompletedExamDTO result = completedExamService.saveCompletedExam(passedExamDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isTrue();
        verify(completedExamRepository, times(2)).save(any(CompletedExam.class)); // Once for initial save, once for certificate update
        verify(certificateService).saveCertificate(any(CertificateDTO.class));
    }
    
    @Test
    void whenSaveFailedExam_thenDoesNotCreateCertificate() {
        // Arrange
        CompletedExam failedExam = new CompletedExam();
        failedExam.setId(2L);
        failedExam.setUserId("user456");
        failedExam.setTrainingId(10L);
        failedExam.setExamId(5L);
        failedExam.setRequiredScore(70);
        failedExam.setScore(60);
        failedExam.setCompletedDate(new Date());
        
        when(completedExamRepository.save(any(CompletedExam.class))).thenReturn(failedExam);
        when(examRepository.findById(5L)).thenReturn(Optional.of(exam));
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));
        
        // Act
        CompletedExamDTO result = completedExamService.saveCompletedExam(failedExamDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isPassed()).isFalse();
        verify(completedExamRepository, times(1)).save(any(CompletedExam.class)); // Only once for initial save
        // Making sure certificate is not created for failed exam
        verify(certificateService, times(0)).saveCertificate(any(CertificateDTO.class));
    }
    
    @Test
    void whenGetCompletedExamsByUserId_thenReturnsList() {
        // Arrange
        String userId = "user123";
        CompletedExam exam1 = new CompletedExam();
        exam1.setId(1L);
        exam1.setUserId(userId);
        exam1.setTrainingId(10L);
        exam1.setExamId(5L);
        
        CompletedExam exam2 = new CompletedExam();
        exam2.setId(3L);
        exam2.setUserId(userId);
        exam2.setTrainingId(11L);
        exam2.setExamId(6L);
        
        List<CompletedExam> exams = Arrays.asList(exam1, exam2);
        
        when(completedExamRepository.findByUserId(userId)).thenReturn(exams);
        when(examRepository.findById(5L)).thenReturn(Optional.of(exam));
        when(examRepository.findById(6L)).thenReturn(Optional.empty());
        when(trainingRepository.findById(10L)).thenReturn(Optional.of(training));
        when(trainingRepository.findById(11L)).thenReturn(Optional.empty());
        
        // Act
        List<CompletedExamDTO> results = completedExamService.getCompletedExamsByUserId(userId);
        
        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getUserId()).isEqualTo(userId);
        assertThat(results.get(1).getUserId()).isEqualTo(userId);
        assertThat(results.get(0).getExamName()).isEqualTo("Safety Exam");
        assertThat(results.get(1).getExamName()).isEqualTo("N/A");
    }
    
    @Test
    void whenGetCompletedExamsByUserIdAndTrainingId_thenReturnsList() {
        // Arrange
        String userId = "user123";
        Long trainingId = 10L;
        
        CompletedExam exam1 = new CompletedExam();
        exam1.setId(1L);
        exam1.setUserId(userId);
        exam1.setTrainingId(trainingId);
        exam1.setExamId(5L);
        
        List<CompletedExam> exams = Arrays.asList(exam1);
        
        when(completedExamRepository.findByUserIdAndTrainingId(userId, trainingId)).thenReturn(exams);
        when(examRepository.findById(5L)).thenReturn(Optional.of(exam));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        
        // Act
        List<CompletedExamDTO> results = completedExamService.getCompletedExamsByUserIdAndTrainingId(userId, trainingId);
        
        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(userId);
        assertThat(results.get(0).getTrainingId()).isEqualTo(trainingId);
        assertThat(results.get(0).getExamName()).isEqualTo("Safety Exam");
        assertThat(results.get(0).getTrainingName()).isEqualTo("Safety Training");
    }
}
