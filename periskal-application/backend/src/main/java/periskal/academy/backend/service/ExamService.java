package periskal.academy.backend.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import periskal.academy.backend.dto.ExamRequest;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Question;
import periskal.academy.backend.jpa.QuestionRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final TrainingRepository trainingRepository;
    private final QuestionRepository questionRepository;

    public Optional<Exam> findById(Long id) {
        return examRepository.findById(id);
    }

    public Optional<Exam> findByTrainingId(Long trainingId) {
        return examRepository.findByTrainingId(trainingId);
    }

    public ExamService(ExamRepository examRepository, TrainingRepository trainingRepository,
            QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.trainingRepository = trainingRepository;
        this.questionRepository = questionRepository;
    }

    public Optional<Exam> getExamByTrainingId(Long trainingId) {
        return examRepository.findByTrainingId(trainingId);
    }

    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }

    public Exam createExam(ExamRequest examRequest) {
        Exam exam = new Exam();
        exam.setExamTitle(examRequest.getExamTitle());
        exam.setExamDescription(examRequest.getExamDescription());

        // Set training if needed
        Training training = trainingRepository.findById(examRequest.getTrainingId())
                .orElseThrow(() -> new RuntimeException("Training not found"));
        exam.setTraining(training);

        // Convert questions
        List<Question> questions = examRequest.getQuestions().stream()
                .map(q -> {
                    Question question = new Question();
                    question.setText(q.getText());
                    question.setExam(exam);
                    return question;
                })
                .collect(Collectors.toList());

        exam.setQuestions(questions);

        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, Exam updatedExam) {
    Optional<Exam> examOpt = examRepository.findById(id);
    if (examOpt.isPresent()) {
        Exam exam = examOpt.get();
        
        exam.setExamTitle(updatedExam.getExamTitle());
        exam.setExamDescription(updatedExam.getExamDescription());

        // Oude vragen verwijderen
        exam.getQuestions().clear();

        for (Question question : updatedExam.getQuestions()) {
            question.setId(null);
            question.setExam(exam); 
            exam.getQuestions().add(question);
        }
        
        return examRepository.save(exam);
    }
    return null;
}


    @Transactional
    public Exam saveExam(Exam exam) {
        // 1. Save exam first
        Exam savedExam = examRepository.save(exam);

        // 2. Save questions with proper type
        if (exam.getQuestions() != null) {
            List<Question> savedQuestions = new ArrayList<>();

            for (periskal.academy.backend.jpa.Question question : exam.getQuestions()) {
                question.setExam(savedExam);
                savedQuestions.add(questionRepository.save(question));
            }

            savedExam.setQuestions(savedQuestions);
        }

        return savedExam;
    }

    public void deleteExam(Long id) {
        Optional<Exam> examOpt = examRepository.findById(id);
        if (examOpt.isEmpty()) {
            throw new RuntimeException("Examen met ID " + id + " bestaat niet.");
        }

        Exam exam = examOpt.get();

        // Unlink from training
        Training training = exam.getTraining(); // assuming you have getTraining()
        if (training != null) {
            training.setExam(null); // or training.setHasExam(false);
            trainingRepository.save(training);
        }

        examRepository.deleteById(id);
    }
}
