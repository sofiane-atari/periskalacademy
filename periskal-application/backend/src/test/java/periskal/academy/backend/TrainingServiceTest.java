package periskal.academy.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.TrainingService;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
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
        training.setName_en("Test Training");

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

    @Test
    void testDeleteTraining_NotFound() {
        // Mock the behavior for when training does not exist
        when(trainingRepository.existsById(1L)).thenReturn(false);

        boolean result = trainingService.deleteTraining(1L);

        assertFalse(result);
        verify(trainingRepository, never()).deleteById(1L);
    }

    @Test
    public void testCreateTraining() {
        Training training = new Training();
        training.setName("Sample Training");
        training.setDescription("Sample Description");

        Module module = new Module();
        module.setNameModule("Module 1");

        Chapter chapter = new Chapter();
        chapter.setNameChapter("Chapter 1");

        Photo photo = new Photo();
        photo.setImageData("data:image/jpeg;base64,placeholder");

        chapter.setPhotos(Collections.singletonList(photo));
        module.setChapters(Collections.singletonList(chapter));
        training.setModules(Collections.singletonList(module));

        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Training savedTraining = trainingService.createTraining(training);

        verify(trainingRepository, times(1)).save(training);

        assertNotNull(savedTraining);
        assertEquals("Sample Training", savedTraining.getName());
        assertFalse(savedTraining.getModules().isEmpty());
        assertFalse(savedTraining.getModules().get(0).getChapters().isEmpty());
        assertFalse(savedTraining.getModules().get(0).getChapters().get(0).getPhotos().isEmpty());
        assertEquals("data:image/jpeg;base64,placeholder",
                savedTraining.getModules().get(0).getChapters().get(0).getPhotos().get(0).getImageData());
    }

    @Test
    void testGetTrainingById_Found() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training foundTraining = trainingService.getTrainingById(1L);

        assertNotNull(foundTraining);
        assertEquals("Test Training", foundTraining.getName_en());
    }

    @Test
    void testGetTrainingById_NotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        Training foundTraining = trainingService.getTrainingById(1L);

        assertNotNull(foundTraining); // Should return an empty training if not found
        assertEquals(null, foundTraining.getName_en());
    }

    @Test
    void testAddModuleWithDto_Success() {
        ModuleDTO moduleDto = new ModuleDTO();
        moduleDto.setNameModule("New Module");
        moduleDto.setDescriptionModule("Module Description");

        // mock trainingRepository to return an existing training
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        // mock moduleRepository to save the module and return a new Module with the
        // correct name
        Module newModule = new Module();
        newModule.setNameModule("New Module");
        newModule.setDescriptionModule("Module Description");
        when(moduleRepository.save(any(Module.class))).thenReturn(newModule);

        Module addedModule = trainingService.addModuleWithDto(1L, moduleDto);

        assertNotNull(addedModule);
        assertEquals("New Module", addedModule.getNameModule());
        assertEquals("Module Description", addedModule.getDescriptionModule());

        // verify that moduleRepository.save() was called with the correct parameters
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void testUpdateTraining_Success() {
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setName_en("Updated Training");

        // Mock trainingRepository to return an existing training
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingRepository.save(updatedTraining)).thenReturn(updatedTraining);

        Training result = trainingService.updateTraining(1L, updatedTraining);

        assertNotNull(result);
        assertEquals("Updated Training", result.getName_en());

        // Verify that trainingRepository.save() was called
        verify(trainingRepository, times(1)).save(updatedTraining);
    }

    @Test
    void testUpdateTraining_NotFound() {
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setName_en("Updated Training");

        // Mock trainingRepository to return an empty Optional
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            trainingService.updateTraining(1L, updatedTraining);
        });

        // Verify that save was not called
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(training);

        // mock the repository to return a list of trainings
        when(trainingRepository.findAllAsList()).thenReturn(trainings);

        // test getAllTrainings method
        List<TrainingDTO> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Training", result.get(0).getName_en());

        // Verify that findAllAsList() method was called once
        verify(trainingRepository, times(1)).findAllAsList();
    }

}
