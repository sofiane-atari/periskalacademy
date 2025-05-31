package periskal.academy.backend.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.CertificateDTO;
import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.jpa.CompletedExam;
import periskal.academy.backend.jpa.CompletedExamRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@Service
@Slf4j
public class CompletedExamService {

    private final CompletedExamRepository completedExamRepository;
    private final ExamRepository examRepository;
    private final TrainingRepository trainingRepository;
    private final CertificateService certificateService;

    public CompletedExamService(
            CompletedExamRepository completedExamRepository,
            ExamRepository examRepository,
            TrainingRepository trainingRepository,
            CertificateService certificateService) {
        this.completedExamRepository = completedExamRepository;
        this.examRepository = examRepository;
        this.trainingRepository = trainingRepository;
        this.certificateService = certificateService;
    }

    @Transactional
    public CompletedExamDTO saveCompletedExam(CompletedExamDTO completedExamDTO) {
        CompletedExam entity = convertToEntity(completedExamDTO);
        CompletedExam saved = completedExamRepository.save(entity);
        CompletedExamDTO result = convertToDTO(saved);

        // Check if the exam was passed
        boolean passed = completedExamDTO.isPassed();

        // Generate certificate if passed
        if (passed) {
            // Get exam details for certificate
            Optional<Exam> exam = examRepository.findById(completedExamDTO.getExamId());
            Optional<Training> training = trainingRepository.findById(completedExamDTO.getTrainingId());

            if (exam.isPresent() && training.isPresent()) {
                // Create certificate object
                CertificateDTO certificateDTO = new CertificateDTO();
                certificateDTO.setUserId(completedExamDTO.getUserId());
                certificateDTO.setName(training.get().getNameEn() + " Certificate");
                certificateDTO.setIssueDate(LocalDate.now());
                certificateDTO.setExpiryDate(LocalDate.now().plusYears(exam.get().getCertificateValidity()));
                certificateDTO.setTraining(training.get().getNameEn());
                certificateDTO.setReadyForDownload(true);

                // Save the certificate
                CertificateDTO savedCertificate = certificateService.saveCertificate(certificateDTO);

                // Update completed exam with certificate reference
                saved.setCertificateId(savedCertificate.getId());
                completedExamRepository.save(saved);

                // Update result DTO
                result.setCertificateId(savedCertificate.getId());
            }
        }

        return result;
    }

    // Method to retrieve all completed exams for a specific user
    public List<CompletedExamDTO> getCompletedExamsByUserId(String userId) {
        List<CompletedExam> exams = completedExamRepository.findByUserId(userId);

        if (exams.isEmpty()) {
            log.info("No completed exams found for user: {}", userId);
            return Collections.emptyList();
        }

        return exams.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Method to retrieve all completed exams for a specific user and training
    public List<CompletedExamDTO> getCompletedExamsByUserIdAndTrainingId(String userId, Long trainingId) {
        List<CompletedExam> exams = completedExamRepository.findByUserIdAndTrainingId(userId, trainingId);
        return exams.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private CompletedExamDTO convertToDTO(CompletedExam completedExam) {
        CompletedExamDTO dto = new CompletedExamDTO();
        dto.setId(completedExam.getId());
        dto.setUserId(completedExam.getUserId());
        dto.setTrainingId(completedExam.getTrainingId());
        dto.setExamId(completedExam.getExamId());
        dto.setRequiredScore(completedExam.getRequiredScore());
        dto.setScore(completedExam.getScore());
        dto.setCompletedDate(completedExam.getCompletedDate());
        dto.setCertificateId(completedExam.getCertificateId());

        dto.setExamName(examRepository.findById(completedExam.getExamId())
                .map(Exam::getExamTitleEn)
                .orElse("N/A"));
        dto.setTrainingName(trainingRepository.findById(completedExam.getTrainingId())
                .map(Training::getNameEn)
                .orElse("N/A"));
        return dto;
    }

    private CompletedExam convertToEntity(CompletedExamDTO dto) {
        CompletedExam entity = new CompletedExam();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setTrainingId(dto.getTrainingId());
        entity.setExamId(dto.getExamId());
        entity.setRequiredScore(dto.getRequiredScore());
        entity.setScore(dto.getScore());
        entity.setCompletedDate(dto.getCompletedDate());
        entity.setCertificateId(dto.getCertificateId());
        return entity;
    }

}
