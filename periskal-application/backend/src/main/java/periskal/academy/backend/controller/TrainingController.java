package periskal.academy.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }
    
@GetMapping
public ResponseEntity<List<TrainingDTO>> getAllTrainings() {
    List<TrainingDTO> trainings = trainingService.getAllTrainings();
    return ResponseEntity.ok(trainings);
}

@GetMapping("/filtered")
public ResponseEntity<List<TrainingDTO>> getFilteredTrainings(
    @RequestParam String role,
    @RequestParam(required = false) List<String> products) {
    List<TrainingDTO> trainings = trainingService.getFilteredTrainings(role, products);
    return ResponseEntity.ok(trainings);
}

    @GetMapping("/{trainingId}")
    public ResponseEntity<Training> getProductsForTraining(@PathVariable Long trainingId) {
        Optional<Training> training = trainingService.getTrainingWithProducts(trainingId);
        return training.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{trainingId}/chapters")
    public ResponseEntity<List<Module>> getModuleWithChapters(@PathVariable Long trainingId) {
        Optional<Training> training = trainingService.getTrainingWithProducts(trainingId);
        return training.map(t -> ResponseEntity.ok(t.getModules())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Training> postTrainingWithModules(@RequestBody Training entity) {
        Training savedTraining = trainingService.createTraining(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTraining);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Training> updateTraining(@PathVariable Long id, @RequestBody Training updatedTraining) {
        try {
            Training savedTraining = trainingService.updateTraining(id, updatedTraining);
            return ResponseEntity.ok(savedTraining);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
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

    @DeleteMapping("/{trainingId}/modules/{moduleId}")
    public ResponseEntity<Training> deleteModule(@PathVariable Long trainingId, @PathVariable Long moduleId) {
        Training updatedTraining = trainingService.deleteModule(trainingId, moduleId);
        return updatedTraining != null ? ResponseEntity.ok(updatedTraining) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/modules")
    public ResponseEntity<Training> updateModules(@PathVariable Long id, @RequestBody List<Module> modules) {
        Training updatedTraining = trainingService.updateModules(id, modules);
        return updatedTraining.getModules().equals(modules) ? ResponseEntity.ok(updatedTraining)
                : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{trainingId}/modules")
    public ResponseEntity<Module> addModuleToTraining(@PathVariable Long trainingId, @RequestBody ModuleDTO moduleDto) {
        Module addedModule = trainingService.addModuleWithDto(trainingId, moduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedModule);
    }
}
