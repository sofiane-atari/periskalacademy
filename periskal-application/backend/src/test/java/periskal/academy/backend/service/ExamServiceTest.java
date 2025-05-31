package periskal.academy.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.dto.ExamRequest;
import periskal.academy.backend.dto.QuestionDTO;
import periskal.academy.backend.jpa.*;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private ExamService examService;

    private Exam exam;
    private Training training;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setId(1L);

        exam = new Exam();
        exam.setId(1L);
        exam.setExamTitleEn("Test Exam");
        exam.setExamDescriptionEn("Sample description");
        exam.setTotalQuestions(2);
        exam.setTraining(training);
    }

    @Test
    void givenExamId_whenFindById_thenReturnsExam() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        Optional<Exam> result = examService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getExamTitleEn()).isEqualTo("Test Exam");
    }

    @Test
    void givenTrainingId_whenFindByTrainingId_thenReturnsExam() {
        when(examRepository.findByTrainingId(1L)).thenReturn(Optional.of(exam));

        Optional<Exam> result = examService.findByTrainingId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTraining().getId()).isEqualTo(1L);
    }

    @Test
    void whenGetAllExams_thenReturnsList() {
        List<Exam> exams = Collections.singletonList(exam);
        when(examRepository.findAll()).thenReturn(exams);

        List<Exam> result = examService.getAllExams();

        assertThat(result).hasSize(1).containsExactly(exam);
    }

    @Test
    void givenValidExamRequest_whenCreateExam_thenReturnsSavedExam() {
        ExamRequest request = new ExamRequest();
        request.setExamTitleEn("New Exam");
        request.setExamDescriptionEn("New Desc");
        request.setTotalQuestions(1);
        request.setTrainingId(1L);

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setTextEn("Question 1");
        request.setQuestions(List.of(questionDTO));

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(examRepository.save(any(Exam.class))).thenReturn(exam);

        Exam result = examService.createExam(request);

        assertThat(result).isNotNull();
        assertThat(result.getExamTitleEn()).isEqualTo("Test Exam"); // Because of stubbed return
        verify(examRepository).save(any(Exam.class));
    }

    @Test
    void givenInvalidTrainingId_whenCreateExam_thenThrowsResourceNotFoundException() {
        ExamRequest request = new ExamRequest();
        request.setTrainingId(999L);
        request.setQuestions(new ArrayList<>());

        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> examService.createExam(request));
    }

    @Test
    void givenExistingExam_whenUpdateExam_thenReturnsUpdatedExam() {
        Exam updatedExam = new Exam();
        updatedExam.setExamTitleEn("Updated Title");
        updatedExam.setExamDescriptionEn("Updated Desc");
        updatedExam.setTotalQuestions(3);

        Question q = new Question();
        q.setTextEn("Updated Question");
        updatedExam.setQuestions(List.of(q));

        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).thenReturn(exam);

        Exam result = examService.updateExam(1L, updatedExam);

        assertThat(result).isNotNull();
        assertThat(result.getQuestions()).hasSize(1);
        verify(examRepository).save(any(Exam.class));
    }

    @Test
    void givenNonExistingExamId_whenUpdateExam_thenReturnsNull() {
        when(examRepository.findById(999L)).thenReturn(Optional.empty());

        Exam result = examService.updateExam(999L, new Exam());

        assertThat(result).isNull();
    }

    @Test
    void givenExistingExam_whenDeleteExam_thenExamIsDeleted() {
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

        examService.deleteExam(1L);

        verify(examRepository).deleteById(1L);
        verify(trainingRepository).save(training);
    }

    @Test
    void givenNonExistingExamId_whenDeleteExam_thenThrowsResourceNotFoundException() {
        when(examRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> examService.deleteExam(999L));
        assertThat(exception.getMessage()).contains("Exam not found with id: 999");
    }
}
