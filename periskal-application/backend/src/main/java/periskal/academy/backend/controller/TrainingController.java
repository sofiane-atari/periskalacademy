package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;

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
}
