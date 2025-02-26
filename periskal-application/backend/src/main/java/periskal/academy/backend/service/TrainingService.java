package periskal.academy.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@Service
public class TrainingService {
    private TrainingRepository trainingRepository;
    private ModuleRepository productRepository;

    public TrainingService(TrainingRepository trainingRepository, ModuleRepository productRepository) {
        this.trainingRepository = trainingRepository;
        this.productRepository = productRepository;
    }

    public List<Training> getAllTrainings() {
        return (List<Training>) trainingRepository.findAll();
    }

    public Optional<Training> getTrainingWithProducts(Long id) {
        return trainingRepository.findById(id);
    }

    public Training createTraining(Training entity) {
        return trainingRepository.save(entity);
    }

    public Training getTrainingById(Long id) {
        Optional<Training> training = trainingRepository.findById(id);
        if (training.isPresent()) {
            return training.get();
        } else {
            return new Training();
        }
    }

    public Training updateTraining(Training training) {
        return trainingRepository.save(training);
    }
}
