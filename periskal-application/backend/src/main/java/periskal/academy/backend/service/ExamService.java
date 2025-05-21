package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.dto.AnswerDTO;
import periskal.academy.backend.dto.ExamRequest;
import periskal.academy.backend.dto.QuestionDTO;
import periskal.academy.backend.jpa.Answer;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.Question;
import periskal.academy.backend.jpa.QuestionRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final TrainingRepository trainingRepository;

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
    }

    public Optional<Exam> getExamByTrainingId(Long trainingId) {
        return examRepository.findByTrainingId(trainingId);
    }

    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }

    @Transactional
    public Exam createExam(ExamRequest examRequest) {
        // 1) Prepare Exam entity
        Exam exam = new Exam();
        exam.setExamTitle(examRequest.getExamTitle());
        exam.setExamDescription(examRequest.getExamDescription());
        exam.setTotalQuestions(examRequest.getTotalQuestions());
        exam.setPrice(examRequest.getPrice());
        exam.setPassingScore(examRequest.getPassingScore());

        // 2) Link Training
        Training training = trainingRepository.findById(examRequest.getTrainingId())
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + examRequest.getTrainingId()));
        exam.setTraining(training);

        // 3) Build Questions + Photos
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO qdto : examRequest.getQuestions()) {
            Question q = new Question();
            q.setText(qdto.getText());
            q.setExam(exam);
            q.setCorrectAnswer(qdto.getCorrectAnswer());

            if (qdto.getPhotos() != null) {
                for (Photo photo : qdto.getPhotos()) {
                    Photo p = new Photo(photo.getImageData());
                    p.setQuestion(q);
                    q.getPhotos().add(p);
                }
            }

            if (qdto.getAnswers() != null) {
                List<Answer> answers = new ArrayList<>();
                for (AnswerDTO adto : qdto.getAnswers()) {
                    Answer a = new Answer();
                    a.setText(adto.getText());
                    a.setQuestion(q);
                    answers.add(a);
                }
                q.setAnswers(answers);
            }

            questions.add(q);
        }
        exam.setQuestions(questions);

        // 5) Save the exam (cascades to questions & photos)
        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, Exam updatedExam) {
        Optional<Exam> examOpt = examRepository.findById(id);
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();

            exam.setExamTitle(updatedExam.getExamTitle());
            exam.setExamDescription(updatedExam.getExamDescription());
            exam.setTotalQuestions(updatedExam.getTotalQuestions());
            exam.setPassingScore(updatedExam.getPassingScore());
    
            // Oude vragen verwijderen
            exam.getQuestions().clear();

            for (Question updatedQuestion : updatedExam.getQuestions()) {
                updatedQuestion.setId(null);
                updatedQuestion.setExam(exam);

                // Answers koppelen aan question
                if (updatedQuestion.getAnswers() != null) {
                    for (Answer answer : updatedQuestion.getAnswers()) {
                        answer.setId(null);
                        answer.setQuestion(updatedQuestion);
                    }
                }

                exam.getQuestions().add(updatedQuestion);
            }

            return examRepository.save(exam);
        }
        return null;
    }

    public void deleteExam(Long id) {
        Optional<Exam> examOpt = examRepository.findById(id);
        if (examOpt.isEmpty()) {
            throw new ResourceNotFoundException("Exam not found with id: " + id);
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
