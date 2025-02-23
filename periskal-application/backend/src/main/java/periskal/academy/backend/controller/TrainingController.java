package periskal.academy.backend.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Product;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping()
    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    @GetMapping("/{trainingId}")
    public Training getProductsForTraining(@PathVariable Long trainingId) {
        Optional<Training> training = trainingService.getTrainingWithProducts(trainingId);
        return training.get();
    }

}
