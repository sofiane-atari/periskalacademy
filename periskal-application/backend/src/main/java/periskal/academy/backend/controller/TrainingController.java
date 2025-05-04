package periskal.academy.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;

@Slf4j
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

        Training training = trainingService.getTrainingById(trainingId);
        return ResponseEntity.ok(training);
    }

    @GetMapping("/{trainingId}/chapters")
    public ResponseEntity<List<Module>> getModuleWithChapters(@PathVariable Long trainingId) {

        Training training = trainingService.getTrainingById(trainingId);
        return ResponseEntity.ok(training.getModules());
    }

    @PostMapping
    public ResponseEntity<Training> postTrainingWithModules(@RequestBody Training entity) {

        Training savedTraining = trainingService.createTraining(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTraining);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Training> updateTraining(@PathVariable Long id,
            @Valid @RequestBody Training updatedTraining) {

        Training savedTraining = trainingService.updateTraining(id, updatedTraining);
        return ResponseEntity.ok(savedTraining);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTraining(@PathVariable Long id) {

        trainingService.deleteTraining(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Training deleted successfully");
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{trainingId}/modules/{moduleId}")
    public ResponseEntity<Training> deleteModule(@PathVariable Long trainingId, @PathVariable Long moduleId) {

        Training updatedTraining = trainingService.deleteModule(trainingId, moduleId);
        return ResponseEntity.ok(updatedTraining);
    }

    @PutMapping("/{id}/modules")
    public ResponseEntity<Training> updateModules(@PathVariable Long id, @RequestBody List<Module> modules) {

        Training updatedTraining = trainingService.updateModules(id, modules);
        return ResponseEntity.ok(updatedTraining);
    }

    @PostMapping("/{trainingId}/modules")
    public ResponseEntity<Module> addModuleToTraining(@PathVariable Long trainingId,
            @Valid @RequestBody ModuleDTO moduleDto) {

        Module addedModule = trainingService.addModuleWithDto(trainingId, moduleDto);
        System.out.println(moduleDto.getNameModule_en());
        System.out.println(moduleDto.getNameModule_nl());
        System.out.println(moduleDto.getNameModule_fr());
        System.out.println(moduleDto.getNameModule_de());
        System.out.println(moduleDto.getDescriptionModule_en());
        System.out.println(moduleDto.getDescriptionModule_nl());

        System.out.println(moduleDto.getDescriptionModule_fr());
        System.out.println(moduleDto.getDescriptionModule_de());

        return ResponseEntity.status(HttpStatus.CREATED).body(addedModule);

    }
}
