package periskal.academy.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.security.Exceptions.General.ResourceNotFoundException;
import periskal.academy.backend.service.TrainingService;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

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
        module.setNameModule_en("Test Module");

        training.setModules(Collections.singletonList(module));
    }

    /*
     * @Test
     * void givenTrainingWithModule_whenDeleteModule_thenModuleIsRemoved() {
     * // Mock trainingRepository behavior
     * when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
     * when(trainingRepository.save(training)).thenReturn(training);
     * 
     * // Test deleteModule method
     * Training updatedTraining = trainingService.deleteModule(1L, 1L);
     * 
     * // Use AssertJ assertions
     * assertThat(updatedTraining).isNotNull();
     * assertThat(updatedTraining.getModules()).isEmpty();
     * 
     * // Verify that save method was called after removal of module
     * verify(trainingRepository, times(1)).save(training);
     * }
     * 
     * @Test
     * void givenNoTraining_whenDeleteModule_thenThrowResourceNotFoundException() {
     * // Mock behavior when the training is not found
     * when(trainingRepository.findById(1L)).thenReturn(Optional.empty());
     * 
     * // Test deleteModule method - should throw ResourceNotFoundException
     * assertThatThrownBy(() -> trainingService.deleteModule(1L, 1L))
     * .isInstanceOf(ResourceNotFoundException.class);
     * 
     * 
     * // Verify that save was never called
     * verify(trainingRepository, never()).save(any());
     * }
     */

    @Test
    void givenExistingTraining_whenDeleteTraining_thenTrainingIsDeleted() {
        // Mock the behavior for deleting a training
        when(trainingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(trainingRepository).deleteById(1L);

        // Act - Should not throw exception
        assertThatCode(() -> trainingService.deleteTraining(1L)).doesNotThrowAnyException();

        verify(trainingRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNonExistingTraining_whenDeleteTraining_thenThrowResourceNotFoundException() {
        // Mock the behavior for when training does not exist
        when(trainingRepository.existsById(1L)).thenReturn(false);

        // Act and Assert
        assertThatThrownBy(() -> trainingService.deleteTraining(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(trainingRepository, never()).deleteById(any());
    }

    @Test
    public void givenTrainingWithModules_whenCreateTraining_thenTrainingIsCreated() {
        // Arrange
        Training training = new Training();
        training.setName_en("Sample Training");
        training.setDescription_en("Sample Description");

        // Create a module and associate it with the training
        Module module = new Module();
        module.setNameModule_en("Module 1");

        // Create chapters and associate them with the module
        Chapter chapter = new Chapter();
        chapter.setNameChapter("Chapter 1");

        // Initialize an empty list of chapters if it's null
        module.setChapters(Collections.singletonList(chapter)); // Ensure it's not null

        // Add the module to the training
        training.setModules(Collections.singletonList(module));

        // Mock the repository to return the training object with modules
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        // Act
        Training savedTraining = trainingService.createTraining(training);

        // Verify that the repository's save method was called once with the training
        // object
        verify(trainingRepository, times(1)).save(training);

        // Assert using AssertJ
        assertThat(savedTraining).isNotNull();
        assertThat(savedTraining.getName_en()).isEqualTo("Sample Training");
        assertThat(savedTraining.getModules()).isNotEmpty(); // Ensure modules are not empty
        assertThat(savedTraining.getModules().get(0).getNameModule_en()).isEqualTo("Module 1"); // Ensure module is
                                                                                                // added
        assertThat(savedTraining.getModules().get(0).getChapters()).isNotEmpty(); // Ensure chapters are not empty
        assertThat(savedTraining.getModules().get(0).getChapters().get(0).getNameChapter()).isEqualTo("Chapter 1"); // Ensure
                                                                                                                    // Chapter
                                                                                                                    // is
                                                                                                                    // added

    }

    @Test
    void givenExistingTraining_whenGetTrainingById_thenTrainingIsReturned() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training foundTraining = trainingService.getTrainingById(1L);

        assertThat(foundTraining).isNotNull();
        assertThat(foundTraining.getName_en()).isEqualTo("Test Training");
    }

    @Test
    void givenNonExistingTraining_whenGetTrainingById_thenThrowResourceNotFoundException() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.getTrainingById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void givenModuleDto_whenAddModule_thenModuleIsAdded() {
        ModuleDTO moduleDto = new ModuleDTO();
        moduleDto.setNameModule("New Module");
        moduleDto.setDescriptionModule("Module Description");

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Module newModule = new Module();
        newModule.setNameModule_en("New Module");
        newModule.setDescriptionModule_en("Module Description");
        when(moduleRepository.save(any(Module.class))).thenReturn(newModule);

        Module addedModule = trainingService.addModuleWithDto(1L, moduleDto);

        assertThat(addedModule).isNotNull();
        assertThat(addedModule.getNameModule_en()).isEqualTo("New Module");
        assertThat(addedModule.getDescriptionModule_en()).isEqualTo("Module Description");

        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void givenExistingTraining_whenUpdateTraining_thenTrainingIsUpdated() {
        // Arrange
        Training existingTraining = new Training();
        existingTraining.setId(1L);
        existingTraining.setName_en("Old Name");
        existingTraining.setDescription_en("Old Description");

        Module module1 = new Module();
        module1.setId(1L);
        module1.setNameModule_en("Module 1");

        Module module2 = new Module();
        module2.setId(2L);
        module2.setNameModule_en("Module 2");

        List<Module> updatedModules = List.of(module1, module2);

        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setName_en("Updated Name");
        updatedTraining.setDescription_en("Updated Description");
        updatedTraining.setModules(updatedModules);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(existingTraining));
        when(trainingRepository.save(existingTraining)).thenReturn(existingTraining);

        // Act
        Training result = trainingService.updateTraining(1L, updatedTraining);

        // Use AssertJ assertions
        assertThat(result).isNotNull();
        assertThat(result.getName_en()).isEqualTo("Updated Name");
        assertThat(result.getDescription_en()).isEqualTo("Updated Description");
        assertThat(result.getModules()).hasSize(2);
        assertThat(result.getModules().get(0).getNameModule_en()).isEqualTo("Module 1");
        assertThat(result.getModules().get(1).getNameModule_en()).isEqualTo("Module 2");

        verify(trainingRepository, times(1)).save(existingTraining);
    }

    @Test
    void givenNonExistingTraining_whenUpdateTraining_thenThrowResourceNotFoundException() {
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setName_en("Updated Training");

        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that ResourceNotFoundException is thrown
        assertThatThrownBy(() -> trainingService.updateTraining(1L, updatedTraining))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void givenTrainingsExist_whenGetAllTrainings_thenTrainingsAreReturned() {
        List<Training> trainings = List.of(training);

        when(trainingRepository.findAllAsList()).thenReturn(trainings);

        List<TrainingDTO> result = trainingService.getAllTrainings();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName_en()).isEqualTo("Test Training");

        verify(trainingRepository, times(1)).findAllAsList();
    }
}
