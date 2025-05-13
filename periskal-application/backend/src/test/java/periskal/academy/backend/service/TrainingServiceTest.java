package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;
import periskal.academy.backend.service.TrainingService;

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
        training.setProductCode("ECIDS");
        training.setId(1L);
        training.setNameEn("Test Training");

        module = new Module();
        module.setId(1L);
        module.setNameModuleEn("Test Module");

        List<Module> modulesList = new ArrayList<>();
        modulesList.add(module);
        training.setModules(modulesList);
    }

    @Test
    void givenTrainingWithModule_whenDeleteModule_thenModuleIsRemoved() {
        // Mock trainingRepository behavior
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingRepository.save(training)).thenReturn(training);

        // Test deleteModule method
        Training updatedTraining = trainingService.deleteModule(1L, 1L);

        // Use AssertJ assertions
        assertThat(updatedTraining).isNotNull();
        assertThat(updatedTraining.getModules()).isEmpty();

        // Verify that save method was called after removal of module
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void givenNoTraining_whenDeleteModule_thenThrowResourceNotFoundException() {
        // Mock behavior when the training is not found
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        // Test deleteModule method - should throw ResourceNotFoundException
        assertThatThrownBy(() -> trainingService.deleteModule(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        // Verify that save was never called
        verify(trainingRepository, never()).save(any());
    }

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
        training.setNameEn("Sample Training");
        training.setDescriptionEn("Sample Description");

        // Create a module and associate it with the training
        Module module = new Module();
        module.setNameModuleEn("Module 1");

        // Create chapters and associate them with the module
        Chapter chapter = new Chapter();
        chapter.setNameChapterEn("Chapter 1");

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
        assertThat(savedTraining.getNameEn()).isEqualTo("Sample Training");
        assertThat(savedTraining.getModules()).isNotEmpty(); // Ensure modules are not empty
        assertThat(savedTraining.getModules().get(0).getNameModuleEn()).isEqualTo("Module 1"); // Ensure module is
                                                                                               // added
        assertThat(savedTraining.getModules().get(0).getChapters()).isNotEmpty(); // Ensure chapters are not empty
        assertThat(savedTraining.getModules().get(0).getChapters().get(0).getNameChapterEn()).isEqualTo("Chapter 1"); // Ensure
                                                                                                                      // Chapter
                                                                                                                      // is
                                                                                                                      // added

    }

    @Test
    void givenExistingTraining_whenGetTrainingById_thenTrainingIsReturned() {
        when(trainingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(training));

        Training foundTraining = trainingService.getTrainingById(1L);

        assertThat(foundTraining).isNotNull();
        assertThat(foundTraining.getNameEn()).isEqualTo("Test Training");
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
        moduleDto.setNameModuleEn("New Module");
        moduleDto.setDescriptionModuleEn("Module Description");
        moduleDto.setNameModuleNl("Nieuwe module");
        moduleDto.setDescriptionModuleNl("Module Beschrijving");
        moduleDto.setNameModuleFr("Frans");
        moduleDto.setDescriptionModuleFr("Module frans");
        moduleDto.setNameModuleDe("New Module");
        moduleDto.setDescriptionModuleDe("Module duits");

        when(trainingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(training));

        Module newModule = new Module();
        newModule.setNameModuleEn("New Module");
        newModule.setDescriptionModuleEn("Module Description");
        when(moduleRepository.save(any(Module.class))).thenReturn(newModule);

        Module addedModule = trainingService.addModuleWithDto(1L, moduleDto);

        assertThat(addedModule).isNotNull();
        assertThat(addedModule.getNameModuleEn()).isEqualTo("New Module");
        assertThat(addedModule.getDescriptionModuleEn()).isEqualTo("Module Description");

        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void givenExistingTraining_whenUpdateTraining_thenTrainingIsUpdated() {
        // Arrange
        Training existingTraining = new Training();
        existingTraining.setId(1L);
        existingTraining.setNameEn("Old Name");
        existingTraining.setDescriptionEn("Old Description");

        Module module1 = new Module();
        module1.setId(1L);
        module1.setNameModuleEn("Module 1");

        Module module2 = new Module();
        module2.setId(2L);
        module2.setNameModuleEn("Module 2");

        List<Module> updatedModules = List.of(module1, module2);

        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setNameEn("Updated Name");
        updatedTraining.setDescriptionEn("Updated Description");
        updatedTraining.setModules(updatedModules);

        when(trainingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(existingTraining));
        when(trainingRepository.save(existingTraining)).thenReturn(existingTraining);

        // Act
        Training result = trainingService.updateTraining(1L, updatedTraining);

        // Use AssertJ assertions
        assertThat(result).isNotNull();
        assertThat(result.getNameEn()).isEqualTo("Updated Name");
        assertThat(result.getDescriptionEn()).isEqualTo("Updated Description");
        assertThat(result.getModules()).hasSize(2);
        assertThat(result.getModules().get(0).getNameModuleEn()).isEqualTo("Module 1");
        assertThat(result.getModules().get(1).getNameModuleEn()).isEqualTo("Module 2");

        verify(trainingRepository, times(1)).save(existingTraining);
    }

    @Test
    void givenNonExistingTraining_whenUpdateTraining_thenThrowResourceNotFoundException() {
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setNameEn("Updated Training");

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
        assertThat(result.get(0).getNameEn()).isEqualTo("Test Training");

        verify(trainingRepository, times(1)).findAllAsList();
    }

    @Test
    void givenAdministratorRole_whenGetFilteredTrainings_thenAllTrainingsAreReturned() {
        // Arrange
        when(trainingRepository.findAllAsList()).thenReturn(List.of(training));

        // Act
        List<TrainingDTO> filteredTrainings = trainingService.getFilteredTrainings("Administrator", List.of());

        // Assert
        assertThat(filteredTrainings).isNotEmpty();
        verify(trainingRepository, times(1)).findAllAsList();
    }

    @Test
    void givenSkipperRole_whenGetFilteredTrainings_thenFilteredTrainingsAreReturned() {
        // Arrange
        when(trainingRepository.findAllAsList()).thenReturn(List.of(training));

        // Act
        List<TrainingDTO> filteredTrainings = trainingService.getFilteredTrainings("Skipper", List.of("ECIDS"));

        // Assert
        assertThat(filteredTrainings).hasSize(1);
        verify(trainingRepository, times(1)).findAllAsList();
    }

    @Test
    void givenInvalidTrainingData_whenCreateTraining_thenThrowValidationException() {
        Training invalidTraining = new Training();
        invalidTraining.setNameEn("");

        assertThatThrownBy(() -> trainingService.createTraining(invalidTraining))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void givenTrainingWithInvalidModule_whenDeleteModule_thenThrowResourceNotFoundException() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        assertThatThrownBy(() -> trainingService.deleteModule(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void givenModuleDtoWithMissingName_whenAddModule_thenThrowValidationException() {
        ModuleDTO invalidModuleDto = new ModuleDTO();
        invalidModuleDto.setDescriptionModuleEn("Missing name");

        when(trainingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(training));

        assertThatThrownBy(() -> trainingService.addModuleWithDto(1L, invalidModuleDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void givenValidTraining_whenUpdateTraining_thenTrainingIsUpdated() {
        // Arrange
        Training existingTraining = new Training();
        existingTraining.setId(1L);
        existingTraining.setNameEn("Old Name");
        existingTraining.setDescriptionEn("Old Description");

        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setNameEn("Updated Name");
        updatedTraining.setDescriptionEn("Updated Description");

        when(trainingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(existingTraining));
        when(trainingRepository.save(existingTraining)).thenReturn(existingTraining);

        // Act
        Training result = trainingService.updateTraining(1L, updatedTraining);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNameEn()).isEqualTo("Updated Name");
        assertThat(result.getDescriptionEn()).isEqualTo("Updated Description");

        verify(trainingRepository, times(1)).save(existingTraining);
    }

    @Test
    void givenValidTrainingIdAndModules_whenUpdateModules_thenModulesAreUpdated() {
        Long trainingId = 1L;
        Training existingTraining = new Training();
        existingTraining.setId(trainingId);
        existingTraining.setModules(new ArrayList<>());

        Module module1 = new Module();
        Module module2 = new Module();
        List<Module> newModules = List.of(module1, module2);

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTraining));
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training updatedTraining = trainingService.updateModules(trainingId, newModules);

        assertThat(updatedTraining.getModules()).containsExactlyInAnyOrder(module1, module2);
        assertThat(module1.getTraining()).isEqualTo(existingTraining);
        assertThat(module2.getTraining()).isEqualTo(existingTraining);
        verify(trainingRepository).save(existingTraining);
    }

    @Test
    void givenInvalidTrainingId_whenUpdateModules_thenThrowResourceNotFoundException() {
        Long trainingId = 999L;
        List<Module> modules = List.of(new Module());

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.updateModules(trainingId, modules))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Training not found with id: " + trainingId);

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void givenDatabaseError_whenUpdateModules_thenThrowRuntimeException() {
        Long trainingId = 1L;
        Training training = new Training();
        training.setId(trainingId);
        training.setModules(new ArrayList<>());
        List<Module> modules = List.of(new Module());

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        when(trainingRepository.save(any())).thenThrow(mock(DataAccessException.class));

        assertThatThrownBy(() -> trainingService.updateModules(trainingId, modules))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update modules for training " + trainingId);

        verify(trainingRepository).save(training);
    }
}
