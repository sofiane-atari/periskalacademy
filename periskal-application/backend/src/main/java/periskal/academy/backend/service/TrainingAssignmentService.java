package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.assignment.TrainingAssignment;
import periskal.academy.backend.jpa.assignment.TrainingAssignmentRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

@Service
@Slf4j
public class TrainingAssignmentService {

    private final TrainingAssignmentRepository assignmentRepository;

    public TrainingAssignmentService(TrainingAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }
    
    @Transactional
    public List<TrainingAssignment> createOrUpdateAssignments(List<TrainingAssignment> assignments) {
        List<TrainingAssignment> processedAssignments = new ArrayList<>();

        for (TrainingAssignment assignment : assignments) {
            Optional<TrainingAssignment> existingAssignment = assignmentRepository
                    .findBySkipperIdAndTrainingId(assignment.getSkipperId(), assignment.getTrainingId());

            if (existingAssignment.isPresent()) {
                // Update existing assignment
                TrainingAssignment existing = existingAssignment.get();
                existing.setAssignedDate(new Date()); 
                existing.setAssignedBy(assignment.getAssignedBy());
                processedAssignments.add(existing);
            } else {
                // New assignment
                if (assignment.getAssignedDate() == null) {
                    assignment.setAssignedDate(new Date());
                }
                processedAssignments.add(assignment);
            }
        }

        try {
            return (List<TrainingAssignment>) assignmentRepository.saveAll(processedAssignments);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create or update assignments due to data integrity violation: {}", e.getMessage());
            throw new ValidationException("Failed to create or update assignments. Please check your data and try again.");
        }
    }

    public List<TrainingAssignment> getAssignmentsForSkipper(String skipperId) {
        if (skipperId == null || skipperId.isBlank()) {
            throw new ValidationException("Skipper ID cannot be null or empty");
        }
        
        return assignmentRepository.findBySkipperId(skipperId);
    }

    public List<TrainingAssignment> getAssignmentsByFleetManager(String fleetManagerId) {
        if (fleetManagerId == null || fleetManagerId.isBlank()) {
            throw new ValidationException("Fleet manager ID cannot be null or empty");
        }
        
        return assignmentRepository.findByAssignedBy(fleetManagerId);
    }
    
    public boolean checkAssignment(String skipperId, Long trainingId) {
      
        Optional<TrainingAssignment> assignment = 
            assignmentRepository.findBySkipperIdAndTrainingId(skipperId, trainingId);
            
        return assignment.isPresent();
    }
    
    @Transactional
    public void deleteAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        
        assignmentRepository.deleteById(assignmentId);
    }
    
    @Transactional
    public void deleteAssignment(String skipperId, Long trainingId) {
        Optional<TrainingAssignment> assignment = 
            assignmentRepository.findBySkipperIdAndTrainingId(skipperId, trainingId);
            
        if (assignment.isEmpty()) {
            throw new ResourceNotFoundException(
                "Assignment not found for skipper: " + skipperId + " and training: " + trainingId);
        }
        
        assignmentRepository.delete(assignment.get());
    }
}
