package periskal.academy.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.TrainingService;

import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private TrainingService trainingService;

    private Training training;
    private Module module;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup dummy training and module
        training = new Training();
        training.setId(1L);
        training.setName("Test Training");

        module = new Module();
        module.setId(1L);
        module.setNameModule("Test Module");

        training.setModules(List.of(module));
    }

    @Test
    void testDeleteModule_Success() {
        // Mock trainingRepository behavior
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        // Test deleteModule method
        Training updatedTraining = trainingService.deleteModule(1L, 1L);

        System.out.println("Modules after deletion: " + updatedTraining.getModules());
        // Verify that the module got deleted
        assertNotNull(updatedTraining);

        assertTrue(updatedTraining.getModules().isEmpty());

        // Verify that save method was called after removal of module
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void testDeleteModule_NotFound() {
        // Mock behavior when the training is not found
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        // Test deleteModule method
        Training updatedTraining = trainingService.deleteModule(1L, 1L);

        // Verify that the returned training is null
        assertNull(updatedTraining);

        // Verify that save was never called
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testDeleteTraining_Success() {
        // Mock the behavior for deleting a training
        when(trainingRepository.existsById(1L)).thenReturn(true);

        boolean result = trainingService.deleteTraining(1L);

        assertTrue(result);
        verify(trainingRepository, times(1)).deleteById(1L);
    }
}
