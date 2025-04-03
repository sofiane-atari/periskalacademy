package periskal.academy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.security.Exceptions.General.ResourceNotFoundException;
import periskal.academy.backend.security.Exceptions.General.ValidationException;

@Slf4j
@Service
public class TrainingService {

    private TrainingRepository trainingRepository;
    private ModuleRepository moduleRepository;

    public TrainingService(TrainingRepository trainingRepository, ModuleRepository moduleRepository) {
        this.trainingRepository = trainingRepository;
        this.moduleRepository = moduleRepository;
    }

    private TrainingDTO convertToDto(Training training) {
        TrainingDTO dto = new TrainingDTO();
        dto.setId(training.getId());
        dto.setProduct_code(training.getProduct_code());
        dto.setName_en(training.getName_en());
        dto.setName_nl(training.getName_nl());
        dto.setName_de(training.getName_de());
        dto.setName_fr(training.getName_fr());
        dto.setDescription_en(training.getDescription_en());
        dto.setDescription_nl(training.getDescription_nl());
        dto.setDescription_fr(training.getDescription_fr());
        dto.setDescription_de(training.getDescription_de());
        return dto;
    }

    @Transactional
    public List<TrainingDTO> getAllTrainings() {
        return trainingRepository.findAllAsList()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Training getTrainingById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));
    }

    public List<TrainingDTO> getFilteredTrainings(String role, List<String> products) {

        if (role == null || role.isBlank()) {
            throw new ValidationException("Role parameter cannot be null or empty.");
        }

        // If Admin/Support, return all trainings
        if ("Administrator".equals(role) || "Support".equals(role)) {
            return getAllTrainings();
        }

        // For Skipper/Fleetmanager with products
        return trainingRepository.findAllAsList()
                .stream()
                .filter(training -> products.stream()
                        .anyMatch(product -> training.getProduct_code().toLowerCase().contains(product.toLowerCase())))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Training createTraining(Training entity) {

        if (entity == null) {
            throw new ValidationException("Training data cannot be null.");
        }
        // We could add more validation here, like checking if the training already
        // exists, etc
        if (entity.getName_en() == null || entity.getName_en().isBlank()) {
            throw new ValidationException("Training name (English) cannot be empty.");
        }

        if (entity.getModules() != null) {
            for (Module module : entity.getModules()) {
                module.setTraining(entity);
                if (module.getChapters() != null) {
                    for (Chapter chapter : module.getChapters()) {
                        chapter.setModule(module);
                        if (chapter.getPhotos() != null) {
                            for (Photo photo : chapter.getPhotos()) {
                                photo.setChapter(chapter);
                            }
                        }
                    }
                }
            }
        }

        try {
            return trainingRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create training due to data integrity violation: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to create training due to database error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save training to the database.", e);
        }
    }

    public Training updateTraining(Long id, Training updatedTraining) {

        if (updatedTraining == null) {
            throw new ValidationException("Updated training data cannot be null.");
        }

        Training existingTraining = getTrainingById(id);

        // Update fields instead of replacing the entire object
        existingTraining.setName_en(updatedTraining.getName_en());
        existingTraining.setDescription_en(updatedTraining.getDescription_en());

        // Update modules correctly
        existingTraining.getModules().clear();
        for (Module module : updatedTraining.getModules()) {
            module.setTraining(existingTraining);
            existingTraining.getModules().add(module);
        }

        try {
            return trainingRepository.save(existingTraining);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to update training {} due to data integrity violation: {}", id, e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to update training {} due to database error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update training " + id + " in the database.", e);
        }
    }

    public void deleteTraining(Long id) {

        if (!trainingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Training not found with id: " + id);
        }
        try {
            trainingRepository.deleteById(id);
            log.info("Successfully deleted Training with ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to delete training {} due to data integrity violation (e.g., related data exists): {}",
                    id, e.getMessage());
            throw new ValidationException("Cannot delete training " + id + ". It might be referenced by other data.",
                    List.of(e.getMessage()));
        } catch (DataAccessException e) {
            log.error("Failed to delete training {} due to database error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete training " + id + " from the database.", e);
        }
    }

    public Training deleteModule(Long trainingId, Long moduleId) {

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + trainingId));

        Module moduleToRemove = training.getModules().stream()
                .filter(module -> module.getId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Module not found with id: " + moduleId + " in training: " + trainingId));

        training.getModules().remove(moduleToRemove);
        moduleToRemove.setTraining(null);

        try {
            return trainingRepository.save(training);
        } catch (DataAccessException e) {
            log.error("Failed to delete module {} from training {} due to database error: {}", moduleId, trainingId,
                    e.getMessage(), e);
            throw new RuntimeException("Failed to delete module " + moduleId + " from training " + trainingId + ".", e);
        }
    }

    public Training updateModules(Long trainingId, List<Module> modules) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + trainingId));

        training.getModules().clear();
        modules.forEach(module -> {
            module.setTraining(training);
            training.getModules().add(module);
        });

        try {
            return trainingRepository.save(training);
        } catch (DataAccessException e) {
            log.error("Failed to update modules for training {} due to database error: {}", trainingId, e.getMessage(),
                    e);
            throw new RuntimeException("Failed to update modules for training " + trainingId + ".", e);
        }

    }

    @Transactional
    public Module addModuleWithDto(Long trainingId, ModuleDTO moduleDto) {

        Training existingTraining = getTrainingById(trainingId);

        Module newModule = new Module();
        newModule.setNameModule_en(moduleDto.getNameModule());
        newModule.setDescriptionModule_en(moduleDto.getDescriptionModule());
        newModule.setTraining(existingTraining);

        try {
            Module savedModule = moduleRepository.save(newModule);
            log.info("Added new module {} to training {}", savedModule.getId(), trainingId);
            return savedModule;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to add module to training {} due to data integrity violation: {}", trainingId,
                    e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to add module to training {} due to database error: {}", trainingId, e.getMessage(), e);
            throw new RuntimeException("Failed to add module to training " + trainingId + ".", e);
        }

    }
}
