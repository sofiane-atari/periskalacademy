package periskal.academy.backend.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.service.PhotoService;
import periskal.academy.backend.security.SecurityRoles;

@Slf4j
@RestController
@RequestMapping("/api/photos")
@PreAuthorize(SecurityRoles.ADMIN)
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

    @PostMapping("/{chapterId}")
    public ResponseEntity<Map<String, String>> addPhotos(@PathVariable Long chapterId,
            @RequestBody List<Photo> photos) {

        photoService.addPhotos(chapterId, photos);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Photo(s) added successfully");
        return ResponseEntity.ok(response);
    }
}
