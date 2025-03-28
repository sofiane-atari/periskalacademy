package periskal.academy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
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

@Slf4j
@Service
public class TrainingService {

    private TrainingRepository trainingRepository;
    private ModuleRepository moduleRepository;
    private final ObjectMapper objectMapper;

    public TrainingService(TrainingRepository trainingRepository, ModuleRepository moduleRepository,
            ObjectMapper objectMapper) {
        this.trainingRepository = trainingRepository;
        this.moduleRepository = moduleRepository;
        this.objectMapper = objectMapper;
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

    public Optional<Training> getTrainingWithProducts(Long id) {
        return trainingRepository.findById(id);
    }

    // TODO: Current implementation checks if product name is contained in
    // training_name_en
    // Future implementation will check a dedicated products field in the Training
    // model
    public List<TrainingDTO> getFilteredTrainings(String role, List<String> products) {
        // If Admin/Support, return all trainings
        if ("Administrator".equals(role)) {
            return getAllTrainings();
        }

        // For other roles (Skipper/Fleetmanager), check products
        if (products == null || products.isEmpty()) {
            log.error("Products list cannot be empty for role: {}", role);
            return new ArrayList<>();
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
        for (Module module : entity.getModules()) {
            for (Chapter chapter : module.getChapters()) {
                for (Photo photo : chapter.getPhotos()) {
                    String base64ImageData = photo.getImageData();

                    photo.setChapter(chapter);
                }
            }
        }
        return trainingRepository.save(entity);
    }

    public Training getTrainingById(Long id) {
        Optional<Training> training = trainingRepository.findById(id);
        if (training.isPresent()) {
            return training.get();
        } else {
            return new Training();
        }
    }

    public Training updateTraining(Long id, Training updatedTraining) {
        Optional<Training> existingTrainingOpt = trainingRepository.findById(id);

        if (existingTrainingOpt.isPresent()) {
            Training existingTraining = existingTrainingOpt.get();

            // Update fields instead of replacing the entire object
            existingTraining.setName_en(updatedTraining.getName_en());
            existingTraining.setDescription_en(updatedTraining.getDescription_en());

            // Update modules correctly
            existingTraining.getModules().clear();
            for (Module module : updatedTraining.getModules()) {
                module.setTraining(existingTraining);
                existingTraining.getModules().add(module);
            }

            return trainingRepository.save(existingTraining);
        } else {
            throw new EntityNotFoundException("Training not found with id: " + id);
        }
    }

    public boolean deleteTraining(Long id) {
        if (trainingRepository.existsById(id)) {
            trainingRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Training deleteModule(Long trainingId, Long moduleId) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        if (trainingOpt.isPresent()) {
            Training training = trainingOpt.get();
            ArrayList<Module> modules = new ArrayList<>(training.getModules());
            boolean removed = modules.removeIf(module -> module.getId().equals(moduleId));

            if (removed) {
                training.setModules(modules);
                trainingRepository.save(training);
                return training;
            }
        }
        return null;
    }

    public Training updateModules(Long trainingId, List<Module> modules) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        if (trainingOpt.isPresent()) {
            Training training = trainingOpt.get();
            if (modules != null) {
                training.setModules(modules);
                trainingRepository.save(training);
                return training;
            }
        }
        return null;
    }

    @Transactional
    public Module addModuleWithDto(Long trainingId, ModuleDTO moduleDto) {
        Training existingTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found with id: " + trainingId));

        Module newModule = new Module();
        newModule.setNameModule(moduleDto.getNameModule());
        newModule.setDescriptionModule(moduleDto.getDescriptionModule());
        newModule.setTraining(existingTraining);

        return moduleRepository.save(newModule);
    }
}
