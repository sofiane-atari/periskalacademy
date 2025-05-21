package periskal.academy.backend.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.video.Video;
import periskal.academy.backend.service.FtpService;
import periskal.academy.backend.service.TrainingService;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private static final Logger LOGGER = LogManager.getLogger();
    private final TrainingService trainingService;
    private final FtpService ftpService;

    public TrainingController(TrainingService trainingService, FtpService ftpService) {
        this.trainingService = trainingService;
        this.ftpService = ftpService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(addedModule);

    }

    // @PostMapping("/video")
    // public ResponseEntity<Map<String, String>>
    // uploadVideoFtp(@RequestParam("video") MultipartFile video) {
    // if (video.isEmpty()) {
    // return ResponseEntity.badRequest().body(Map.of("error", "No video file
    // provided"));
    // }

    // String filename = video.getOriginalFilename();
    // String sanitizedFilename = filename;

    // try {
    // // Start the async upload process
    // CompletableFuture<URI> futureUri = ftpService.uploadFile(video,
    // sanitizedFilename);

    // // Wait for completion with timeout
    // URI ftpUri = futureUri.get(60, TimeUnit.SECONDS); // 60 second timeout

    // // Extract and return the file path from the URI
    // String filePath = ftpUri.getPath();

    // // Create a response with both the full URI and just the file path
    // Map<String, String> response = new HashMap<>();
    // response.put("ftpPath", ftpUri.toString());
    // response.put("filePath", filePath);
    // response.put("fileName", sanitizedFilename);
    // response.put("status", "uploaded");

    // LOGGER.info("Video uploaded successfully. Path: {}", filePath);
    // return ResponseEntity.ok(response);
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // LOGGER.error("Upload interrupted: {}", e.getMessage());
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(Map.of("error", "Upload was interrupted"));
    // } catch (ExecutionException e) {
    // Throwable cause = e.getCause();
    // LOGGER.error("Error during FTP upload: {}", cause.getMessage(), cause);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(Map.of("error", "Failed to upload video to FTP: " +
    // cause.getMessage()));
    // } catch (TimeoutException e) {
    // LOGGER.error("Upload timed out: {}", e.getMessage());
    // return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
    // .body(Map.of("error", "Upload timed out. Please try again later."));
    // } catch (Exception e) {
    // LOGGER.error("Unexpected error during upload: {}", e.getMessage(), e);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(Map.of("error", "Failed to upload: " + e.getMessage()));
    // }
    // }

    // @GetMapping("/video/stream")
    // public ResponseEntity<Resource> streamVideo(@RequestParam String videoPath,
    // HttpServletRequest request) {
    // try {
    // Resource videoResource = ftpService.retrieveFileAsResource(videoPath);

    // if (videoResource != null) {
    // HttpHeaders headers = new HttpHeaders();

    // headers.setContentType(MediaType.parseMediaType("video/mp4"));

    // headers.setContentDispositionFormData("inline", videoPath);

    // return ResponseEntity.status(HttpStatus.OK)
    // .headers(headers)
    // .body(videoResource);

    // } else {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }

    // } catch (FileNotFoundException e) {
    // LOGGER.error("Video file not found on FTP: {}", videoPath, e);
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // } catch (IOException e) {
    // LOGGER.error("Error streaming video from FTP: {}", videoPath, e);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // } catch (Exception e) {
    // LOGGER.error("An unexpected error occurred while streaming video: {}",
    // videoPath, e);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }
    @PostMapping("/video")
    public ResponseEntity<Video> uploadVideo(@RequestParam("video") MultipartFile video) {
        if (video == null || video.isEmpty()) {
            LOGGER.error("No video file provided");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Video savedVideo = trainingService.uploadVideo(video);
            LOGGER.info("Video uploaded successfully. ID: {}, Path: {}", savedVideo.getId(), savedVideo.getFilePath());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVideo);
        } catch (Exception e) {
            LOGGER.error("Failed to upload video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/video/stream")
    public ResponseEntity<Resource> streamVideo(@RequestParam String videoPath, HttpServletRequest request) {
        try {
            Resource videoResource = trainingService.streamVideoResource(videoPath);
            if (videoResource == null || !videoResource.exists()) {
                LOGGER.error("Video file not found: {}", videoPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentDispositionFormData("inline", videoPath);

            LOGGER.info("Streaming video: {}", videoPath);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoResource);
        } catch (Exception e) {
            LOGGER.error("Error streaming video from path {}: {}", videoPath, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
