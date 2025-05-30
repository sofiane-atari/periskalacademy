package periskal.academy.backend.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.PhotoRepository;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.jpa.video.Video;
import periskal.academy.backend.jpa.video.VideoRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

@Slf4j
@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final ModuleRepository moduleRepository;
    private final PhotoRepository photoRepository;
    private final VideoRepository videoRepository;
    private final FtpService ftpService;

    private static final String TRAINING_NOT_FOUND_MSG = "Training not found with id: ";


    public TrainingService(TrainingRepository trainingRepository, ModuleRepository moduleRepository,
            PhotoRepository photoRepository, VideoRepository videoRepository, FtpService ftpService) {
        this.trainingRepository = trainingRepository;
        this.moduleRepository = moduleRepository;
        this.photoRepository = photoRepository;
        this.videoRepository = videoRepository;
        this.ftpService = ftpService;
    }

    private TrainingDTO convertToDto(Training training) {
        TrainingDTO dto = new TrainingDTO();
        dto.setId(training.getId());
        dto.setProductCode(training.getProductCode());
        dto.setNameEn(training.getNameEn());
        dto.setNameNl(training.getNameNl());
        dto.setNameDe(training.getNameDe());
        dto.setNameFr(training.getNameFr());
        dto.setDescriptionEn(training.getDescriptionEn());
        dto.setDescriptionNl(training.getDescriptionNl());
        dto.setDescriptionFr(training.getDescriptionFr());
        dto.setDescriptionDe(training.getDescriptionDe());
        dto.setModules(training.getModules() != null
                ? training.getModules().stream().map(module -> {
                    ModuleDTO mdto = new ModuleDTO();
                    mdto.setId(module.getId());
                    mdto.setDescriptionModuleEn(module.getDescriptionModuleEn());
                    mdto.setDescriptionModuleDe(module.getDescriptionModuleDe());
                    mdto.setDescriptionModuleFr(module.getDescriptionModuleFr());
                    mdto.setDescriptionModuleNl(module.getDescriptionModuleNl());
                    mdto.setNameModuleEn(module.getNameModuleEn());
                    mdto.setNameModuleDe(module.getNameModuleDe());
                    mdto.setNameModuleFr(module.getNameModuleFr());
                    mdto.setNameModuleNl(module.getNameModuleNl());

                    return mdto;
                }).collect(Collectors.toList())
                : new ArrayList<>());
        return dto;
    }

    @Transactional
    public List<TrainingDTO> getAllTrainings() {
        return trainingRepository.findAllAsList()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public Training getTrainingById(Long id) {
        return trainingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(TRAINING_NOT_FOUND_MSG + id));
    }

    public List<TrainingDTO> getFilteredTrainings(String role, List<String> products) {
        List<Training> trainings = trainingRepository.findAllAsList();
        if (role == null || role.isBlank()) {
            throw new ValidationException("Role parameter cannot be null or empty.");
        }

        // If Admin/Support, return all trainings
        if ("Administrator".equals(role) || "Support".equals(role)) {
            return trainings.stream()
                    .map(this::convertToDto)
                    .toList();
        }

        // For Skipper/Fleetmanager with products
        return trainings.stream()
                .filter(training -> products.stream()
                        .anyMatch(product -> training.getProductCode().toLowerCase().contains(product.toLowerCase())))
                .map(this::convertToDto)
                .toList();
    }

    // #region Create training method and it's helper methods
    @Transactional
    public Training createTraining(Training entity) {
        
        validateTraining(entity);
        processModules(entity);

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

    private void validateTraining(Training entity) {
        if (entity == null) {
            throw new ValidationException("Training data cannot be null.");
        }
        if (entity.getNameEn() == null || entity.getNameEn().isBlank()) {
            throw new ValidationException("Training name (English) cannot be empty.");
        }
    }

    private void processModules(Training entity) {
        if (entity.getModules() == null) return;

        for (Module module : entity.getModules()) {
            module.setTraining(entity);
            processChapters(module);
        }
    }

    private void processChapters(Module module) {
        if (module.getChapters() == null) return;

        for (Chapter chapter : module.getChapters()) {
            chapter.setModule(module);
            loadChapterVideos(chapter);
            linkPhotos(chapter);
        }
    }

    private void loadChapterVideos(Chapter chapter) {
        chapter.setVideoEn(resolveVideo(chapter.getVideoEn(), "EN"));
        chapter.setVideoDe(resolveVideo(chapter.getVideoDe(), "DE"));
        chapter.setVideoFr(resolveVideo(chapter.getVideoFr(), "FR"));
        chapter.setVideoNl(resolveVideo(chapter.getVideoNl(), "NL"));
    }

    private Video resolveVideo(Video video, String langCode) {
        if (video == null) return null;
        return videoRepository.findById(video.getId())
            .orElseThrow(() -> new RuntimeException("Video " + langCode + " not found: " + video.getId()));
    }

    private void linkPhotos(Chapter chapter) {
        if (chapter.getPhotos() == null) return;
        for (Photo photo : chapter.getPhotos()) {
            photo.setChapter(chapter);
            photoRepository.save(photo);
        }
    }
    // #endregion


    @Transactional
    public Training updateTraining(Long id, Training updatedTraining) {
        if (updatedTraining == null) {
            throw new ValidationException("Updated training data cannot be null.");
        }

        // Fetch existing training (fully initialized)
        Training existingTraining = getTrainingById(id);

        // Update basic fields
        existingTraining.setNameEn(updatedTraining.getNameEn());
        existingTraining.setDescriptionEn(updatedTraining.getDescriptionEn());
        existingTraining.setNameNl(updatedTraining.getNameNl());
        existingTraining.setDescriptionNl(updatedTraining.getDescriptionNl());
        existingTraining.setNameFr(updatedTraining.getNameFr());
        existingTraining.setDescriptionFr(updatedTraining.getDescriptionFr());
        existingTraining.setNameDe(updatedTraining.getNameDe());
        existingTraining.setDescriptionDe(updatedTraining.getDescriptionDe());

        // Clear and replace modules (cascade + orphanRemoval will handle deletions)
        existingTraining.getModules().clear();

        for (Module updatedModule : updatedTraining.getModules()) {
            updatedModule.setTraining(existingTraining); // Set owning side

            for (Chapter updatedChapter : updatedModule.getChapters()) {
                updatedChapter.setModule(updatedModule); // Set owning side
            }
            existingTraining.getModules().add(updatedModule);
        }

        try {
            // Save the updated training
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
            throw new ResourceNotFoundException("Cannot delete. " + TRAINING_NOT_FOUND_MSG  + id);
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
                .orElseThrow(() -> new ResourceNotFoundException(TRAINING_NOT_FOUND_MSG + trainingId));

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
                .orElseThrow(() -> new ResourceNotFoundException(TRAINING_NOT_FOUND_MSG + trainingId));

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

        if (moduleDto.getNameModuleEn() == null || moduleDto.getNameModuleEn().isEmpty()) {
            throw new ValidationException("Module name (English) cannot be empty.");
        }
        if (moduleDto.getNameModuleNl() == null || moduleDto.getNameModuleNl().isEmpty()) {
            throw new ValidationException("Module name (Dutch) cannot be empty.");
        }
        if (moduleDto.getNameModuleFr() == null || moduleDto.getNameModuleFr().isEmpty()) {
            throw new ValidationException("Module name (French) cannot be empty.");
        }
        if (moduleDto.getNameModuleDe() == null || moduleDto.getNameModuleDe().isEmpty()) {
            throw new ValidationException("Module name (German) cannot be empty.");
        }

        Training existingTraining = getTrainingById(trainingId);

        Module newModule = new Module();
        newModule.setNameModuleEn(moduleDto.getNameModuleEn());
        newModule.setDescriptionModuleEn(moduleDto.getDescriptionModuleEn());
        newModule.setNameModuleNl(moduleDto.getNameModuleNl());
        newModule.setDescriptionModuleNl(moduleDto.getDescriptionModuleNl());
        newModule.setNameModuleFr(moduleDto.getNameModuleFr());
        newModule.setDescriptionModuleFr(moduleDto.getDescriptionModuleFr());
        newModule.setNameModuleDe(moduleDto.getNameModuleDe());
        newModule.setDescriptionModuleDe(moduleDto.getDescriptionModuleDe());
        newModule.setChapters(moduleDto.getChapters());
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

    @Transactional
    public Video uploadVideo(MultipartFile videoFile) throws Exception {
        if (videoFile == null || videoFile.isEmpty()) {
            log.error("No video file provided");
            throw new ValidationException("No video file provided.");
        }

        String originalName = videoFile.getOriginalFilename();
        String storedName = UUID.randomUUID().toString() + "_" + originalName;

        CompletableFuture<URI> futureUri = ftpService.uploadFile(videoFile, storedName);
        URI ftpUri;
        try {
            ftpUri = futureUri.get(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Video upload interrupted: {}", e.getMessage());
            throw new RuntimeException("Video upload was interrupted.", e);
        } catch (ExecutionException e) {
            log.error("Error during FTP upload: {}", e.getCause().getMessage());
            throw new RuntimeException("Failed to upload video to FTP.", e.getCause());
        } catch (TimeoutException e) {
            log.error("Video upload timed out: {}", e.getMessage());
            throw new RuntimeException("Video upload timed out.");
        }

        Video video = new Video();
        video.setOriginalName(originalName);
        video.setStoredName(storedName);
        video.setFilePath(ftpUri.getPath());
        video.setFileSize(videoFile.getSize());
        video.setMimeType(videoFile.getContentType());
        video.setUploadDate(LocalDateTime.now());

        try {
            Video savedVideo = videoRepository.save(video);
            log.info("Video uploaded and saved successfully: {}", savedVideo.getId());
            return savedVideo;
        } catch (DataAccessException e) {
            log.error("Failed to save video to database: {}", e.getMessage());
            throw new RuntimeException("Failed to save video to the database.", e);
        }
    }

    public Resource streamVideoResource(String videoPath) throws Exception {
        try {
            Resource resource = ftpService.downloadFile(videoPath);
            if (!resource.exists() || !resource.isReadable()) {
                log.error("Resource not found or unreadable for path: {}", videoPath);
                throw new IOException("Resource not found or unreadable: " + videoPath);
            }
            log.info("Successfully retrieved video resource for path: {}", videoPath);
            return resource;
        } catch (Exception e) {
            log.error("Failed to stream video from path {}: {}", videoPath, e.getMessage(), e);
            throw new RuntimeException("Failed to stream video.", e);
        }
    }

    // Deprecated method (for backward compatibility, can be removed if not used)
    public byte[] streamVideo(String videoPath) throws Exception {
        return streamVideoResource(videoPath).getInputStream().readAllBytes();
    }
}
