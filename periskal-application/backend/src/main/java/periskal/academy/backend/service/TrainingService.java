package periskal.academy.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import periskal.academy.backend.jpa.ProductRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;

@Service
public class TrainingService {
    private TrainingRepository trainingRepository;
    private ProductRepository productRepository;

    public TrainingService(TrainingRepository trainingRepository, ProductRepository productRepository) {
        this.trainingRepository = trainingRepository;
        this.productRepository = productRepository;
    }

    public List<Training> getAllTrainings() {
        return (List<Training>) trainingRepository.findAll();
    }
}
