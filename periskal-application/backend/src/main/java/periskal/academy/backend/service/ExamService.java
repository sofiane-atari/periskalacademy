package periskal.academy.backend.service;

import org.springframework.stereotype.Service;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final TrainingRepository trainingRepository;

    public ExamService(ExamRepository examRepository, TrainingRepository trainingRepository) {
        this.examRepository = examRepository;
        this.trainingRepository = trainingRepository;
    }

    public Optional<Exam> getExamByTrainingId(Long trainingId) {
        return examRepository.findByTrainingId(trainingId);
    }

    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }

    public Exam createExamForTraining(Long trainingId, Exam exam) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        Training training = null;
        if (trainingOpt.isEmpty()) {
            throw new RuntimeException("Training not found");
        }

        if (trainingOpt.isPresent()) {
            training = trainingOpt.get();
        }
        // Check if training already has an exam
        if (examRepository.existsByTrainingId(trainingId)) {
            throw new RuntimeException("This training already has an exam.");
        }

        exam.setTraining(training);
        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, Exam updatedExam) {
        Optional<Exam> examOpt = examRepository.findById(id);
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();
            exam.setExamTitle(updatedExam.getExamTitle());
            exam.setExamDescription(updatedExam.getExamDescription());
            return examRepository.save(exam);
        }
        return null;
    }

    public boolean deleteExam(Long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
