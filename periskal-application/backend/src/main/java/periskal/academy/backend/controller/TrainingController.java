package periskal.academy.backend.controller;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

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

    @PostMapping("/create")
    public ResponseEntity<Training> postTrainingWithModules(@RequestBody Training entity) {
        Training savedTraining = trainingService.createTraining(entity);
        return new ResponseEntity<>(savedTraining, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Training> updateTrainingWithModules(@PathVariable Long id,
            @RequestBody Training updatedTraining) {
        Training existingTraining = trainingService.getTrainingById(id);

        existingTraining.setName(updatedTraining.getName());
        existingTraining.setDescription(updatedTraining.getDescription());
        existingTraining.setModules(updatedTraining.getModules());

        Training savedTraining = trainingService.updateTraining(existingTraining);
        return new ResponseEntity<>(savedTraining, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> deleteTraining(@PathVariable Long id) {
        boolean deleted = trainingService.deleteTraining(id);
        if (deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Training deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("delete/{trainingId}/modules/{moduleId}")
    public ResponseEntity<Training> deleteModule(@PathVariable Long trainingId, @PathVariable Long moduleId) {
        Training updatedTraining = trainingService.deleteModule(trainingId, moduleId);
        if (updatedTraining != null) {
            return ResponseEntity.ok(updatedTraining);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
