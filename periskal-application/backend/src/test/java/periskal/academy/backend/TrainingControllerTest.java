package periskal.academy.backend;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.controller.TrainingController;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    private Training training;
    private Module module;
    private List<TrainingDTO> trainingDTOs;
    private ModuleDTO moduleDTO;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setId(1L);
        training.setName_en("Test Training");

        module = new Module();
        module.setId(1L);
        module.setNameModule("Test Module");

        trainingDTOs = new ArrayList<>();
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setId(1L);
        trainingDTO.setName_en("Test Training");
        trainingDTOs.add(trainingDTO);

        moduleDTO = new ModuleDTO();
        moduleDTO.setNameModule("New Module");
    }

    @Test
    void givenTrainingsExist_whenGetAllTrainings_thenReturnsTrainings() {
        // Arrange
        when(trainingService.getAllTrainings()).thenReturn(trainingDTOs);

        // Act
        ResponseEntity<List<TrainingDTO>> response = trainingController.getAllTrainings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    // Test for getProductsForTraining
    @Test
    void givenValidTrainingId_whenGetProductsForTraining_thenReturnsTraining() {
        // Arrange
        when(trainingService.getTrainingWithProducts(1L)).thenReturn(Optional.of(training));

        // Act
        ResponseEntity<Training> response = trainingController.getProductsForTraining(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    // Test for getModuleWithChapters
    @Test
    void givenValidTrainingId_whenGetModuleWithChapters_thenReturnsModules() {
        // Arrange
        training.setModules(Collections.singletonList(module));
        when(trainingService.getTrainingWithProducts(1L)).thenReturn(Optional.of(training));

        // Act
        ResponseEntity<List<Module>> response = trainingController.getModuleWithChapters(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    // Test for postTrainingWithModules
    @Test
    void givenNewTraining_whenPostTrainingWithModules_thenReturnsCreatedTraining() {
        // Arrange
        when(trainingService.createTraining(any(Training.class))).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.postTrainingWithModules(training);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    // Test for updateTraining
    @Test
    void givenValidTrainingId_whenUpdateTraining_thenReturnsUpdatedTraining() {
        // Arrange
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setName_en("Updated Training");

        when(trainingService.updateTraining(1L, updatedTraining)).thenReturn(updatedTraining);

        // Act
        ResponseEntity<Training> response = trainingController.updateTraining(1L, updatedTraining);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Training", response.getBody().getName_en());
    }

    // Test for deleteTraining
    @Test
    void givenValidTrainingId_whenDeleteTraining_thenReturnsSuccessMessage() {
        // Arrange
        when(trainingService.deleteTraining(1L)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = trainingController.deleteTraining(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Training deleted successfully", response.getBody().get("message"));
    }

    // Test for deleteModule
    @Test
    void givenValidTrainingIdAndModuleId_whenDeleteModule_thenReturnsUpdatedTraining() {
        // Arrange
        when(trainingService.deleteModule(1L, 1L)).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.deleteModule(1L, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    // Test for updateModules
    @Test
    void givenValidModules_whenUpdateModules_thenReturnsUpdatedTraining() {
        // Arrange
        List<Module> modules = Collections.singletonList(module);
        training.setModules(modules);
        when(trainingService.updateModules(1L, modules)).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.updateModules(1L, modules);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(modules, response.getBody().getModules());
    }

    // Test for addModuleToTraining
    @Test
    void givenValidTrainingIdAndModule_whenAddModuleToTraining_thenReturnsCreatedModule() {
        // Arrange
        when(trainingService.addModuleWithDto(1L, moduleDTO)).thenReturn(module);

        // Act
        ResponseEntity<Module> response = trainingController.addModuleToTraining(1L, moduleDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Module", response.getBody().getNameModule());
    }
}