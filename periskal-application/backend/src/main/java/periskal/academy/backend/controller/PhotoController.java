package periskal.academy.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.service.PhotoService;
import periskal.academy.backend.service.TrainingService;

@Slf4j
@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Map<String, String>> deleteTraining(@PathVariable Long photoId) {

        photoService.deletePhoto(photoId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Photo deleted successfully");
        return ResponseEntity.ok(response);
    }

}
