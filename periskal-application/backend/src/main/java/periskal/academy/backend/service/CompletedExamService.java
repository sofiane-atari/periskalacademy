package periskal.academy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import periskal.academy.backend.jpa.CompletedExam;
import periskal.academy.backend.jpa.CompletedExamRepository;
import periskal.academy.backend.dto.CompletedExamDTO;

@Service
public class CompletedExamService {
    
    private final CompletedExamRepository completedExamRepository;

    public CompletedExamService(CompletedExamRepository completedExamRepository) {
        this.completedExamRepository = completedExamRepository;
    }

    public CompletedExamDTO saveCompletedExam(CompletedExamDTO completedExamDTO) {
        CompletedExam entity = convertToEntity(completedExamDTO);
        CompletedExam saved = completedExamRepository.save(entity);
        return convertToDTO(saved);
    }

    // Method to retrieve all completed exams for a specific user
    public List<CompletedExamDTO> getCompletedExamsByUserId(String userId) {
        List<CompletedExam> exams = completedExamRepository.findByUserId(userId);
        return exams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Method to retrieve all completed exams for a specific user and training
    public List<CompletedExamDTO> getCompletedExamsByUserIdAndTrainingId(String userId, Long trainingId) {
        List<CompletedExam> exams = completedExamRepository.findByUserIdAndTrainingId(userId, trainingId);
        return exams.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
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
        return entity;
    }
    


}
