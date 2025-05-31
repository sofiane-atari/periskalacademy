package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.assignment.TrainingAssignment;
import periskal.academy.backend.jpa.assignment.TrainingAssignmentRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

@Service
@Slf4j
public class TrainingAssignmentService {

    private final TrainingAssignmentRepository assignmentRepository;
    private final ExamRepository examRepository;
    private final UserExamAttemptRepository userExamAttemptRepository;

    public TrainingAssignmentService(
            TrainingAssignmentRepository assignmentRepository,
            ExamRepository examRepository,
            UserExamAttemptRepository userExamAttemptRepository) {
        this.assignmentRepository = assignmentRepository;
        this.examRepository = examRepository;
        this.userExamAttemptRepository = userExamAttemptRepository;
    }

    @Transactional
    public List<TrainingAssignment> createOrUpdateAssignments(List<TrainingAssignment> assignments) {
        List<TrainingAssignment> processedAssignments = new ArrayList<>();
        List<UserExamAttempt> examAttemptsToSave = new ArrayList<>();

        for (TrainingAssignment assignment : assignments) {
            log.info("Processing assignment for userId: {}, trainingId: {}",
                    assignment.getSkipperId(), assignment.getTrainingId());

            Optional<TrainingAssignment> existingAssignment = assignmentRepository
                    .findBySkipperIdAndTrainingId(assignment.getSkipperId(), assignment.getTrainingId());

            if (existingAssignment.isPresent()) {
                // Update existing assignment
                TrainingAssignment existing = existingAssignment.get();
                existing.setAssignedDate(new Date());
                existing.setAssignedBy(assignment.getAssignedBy());
                processedAssignments.add(existing);
                log.info("Updated existing assignment for userId: {}, trainingId: {}",
                        assignment.getSkipperId(), assignment.getTrainingId());
            } else {
                // Create new assignment
                if (assignment.getAssignedDate() == null) {
                    assignment.setAssignedDate(new Date());
                }
                processedAssignments.add(assignment);
                log.info("Created new assignment for userId: {}, trainingId: {}",
                        assignment.getSkipperId(), assignment.getTrainingId());

                // Create or update exam attempt
                Optional<Exam> examOpt = examRepository.findByTrainingId(assignment.getTrainingId());
                if (examOpt.isPresent()) {
                    Exam exam = examOpt.get();
                    log.info("Found exam for trainingId: {}: examId={}, examAttempts={}",
                            assignment.getTrainingId(), exam.getId(), exam.getExamAttempts());

                    // Validate examAttempts
                    int totalAttempts = exam.getExamAttempts();
                    if (totalAttempts <= 0) {
                        totalAttempts = 1;
                        log.warn("Invalid examAttempts ({}) for examId: {}. Defaulting to 1.",
                                exam.getExamAttempts(), exam.getId());
                    }

                    // Check for existing exam attempts
                    List<UserExamAttempt> existingAttempts = userExamAttemptRepository
                            .findAllByUserIdAndExamId(assignment.getSkipperId(), exam.getId().toString());
                    Optional<UserExamAttempt> activeAttempt = existingAttempts.stream()
                            .filter(UserExamAttempt::hasAttemptsLeft)
                            .findFirst();

                    if (activeAttempt.isEmpty()) {
                        UserExamAttempt examAttempt;
                        if (!existingAttempts.isEmpty()) {
                            // Reset the first existing attempt
                            examAttempt = existingAttempts.get(0);
                            examAttempt.setTotalAttempts(totalAttempts);
                            examAttempt.setUsedAttempts(0);
                            log.info("Reset existing exam attempt for userId: {}, examId: {}, totalAttempts={}",
                                    assignment.getSkipperId(), exam.getId(), totalAttempts);
                        } else {
                            // Create new attempt
                            examAttempt = new UserExamAttempt();
                            examAttempt.setUserId(assignment.getSkipperId());
                            examAttempt.setExamId(exam.getId().toString());
                            examAttempt.setTotalAttempts(totalAttempts);
                            examAttempt.setUsedAttempts(0);
                            log.info("Created new exam attempt for userId: {}, examId: {}, totalAttempts={}",
                                    assignment.getSkipperId(), exam.getId(), totalAttempts);
                        }
                        examAttemptsToSave.add(examAttempt);
                    } else {
                        log.info("Active exam attempt exists for userId: {}, examId: {}, remainingAttempts={}",
                                assignment.getSkipperId(), exam.getId(), activeAttempt.get().getRemainingAttempts());
                    }
                } else {
                    log.warn("No exam found for trainingId: {}. Skipping exam attempt creation.",
                            assignment.getTrainingId());
                }
            }
        }

        try {
            // Save training assignments
            List<TrainingAssignment> savedAssignments = (List<TrainingAssignment>) assignmentRepository
                    .saveAll(processedAssignments);
            log.info("Saved {} training assignments", savedAssignments.size());

            // Save exam attempts
            if (!examAttemptsToSave.isEmpty()) {
                Iterable<UserExamAttempt> savedAttemptsIterable = userExamAttemptRepository.saveAll(examAttemptsToSave);
                List<UserExamAttempt> savedAttempts = StreamSupport.stream(savedAttemptsIterable.spliterator(), false)
                        .collect(Collectors.toList());
                log.info("Saved {} exam attempts", savedAttempts.size());
                for (UserExamAttempt savedAttempt : savedAttempts) {
                    log.debug("Saved exam attempt: userId={}, examId={}, totalAttempts={}, usedAttempts={}",
                            savedAttempt.getUserId(), savedAttempt.getExamId(),
                            savedAttempt.getTotalAttempts(), savedAttempt.getUsedAttempts());
                }
            }
            return savedAssignments;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving assignments/exam attempts: {}", e.getMessage(), e);
            throw new ValidationException("Failed to save assignments or exam attempts: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error saving assignments/exam attempts: {}", e.getMessage(), e);
            throw new ValidationException("Unexpected error: " + e.getMessage());
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
        Optional<TrainingAssignment> assignment = assignmentRepository.findBySkipperIdAndTrainingId(skipperId,
                trainingId);
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
        Optional<TrainingAssignment> assignment = assignmentRepository.findBySkipperIdAndTrainingId(skipperId,
                trainingId);
        if (assignment.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Assignment not found for skipper: " + skipperId + " and training: " + trainingId);
        }
        assignmentRepository.delete(assignment.get());
    }
}