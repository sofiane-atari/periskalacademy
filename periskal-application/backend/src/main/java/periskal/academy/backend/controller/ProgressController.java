package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.progress.ChapterCompletion;
import periskal.academy.backend.dto.progress.TrainingProgressDTO;
import periskal.academy.backend.security.SecurityRoles;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.service.ProgressService;

@RestController
@RequestMapping("/api/progress")
@Slf4j
public class ProgressController {
    
    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/complete-chapter")
    @PreAuthorize(SecurityRoles.ADMIN_SUPPORT_SKIPPER)
    public ResponseEntity<TrainingProgressDTO> completeChapter(@RequestBody ChapterCompletion request) {
        TrainingProgressDTO result = progressService.completeChapter(
            request.getUserId(),
            request.getTrainingId(),
            request.getModuleId(),
            request.getChapterId()
        );
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<List<TrainingProgressDTO>> getUserProgress(@PathVariable String userId) {
        List<TrainingProgressDTO> progress = progressService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/training/{trainingId}/user/{userId}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<TrainingProgressDTO> getTrainingProgress(
            @PathVariable Long trainingId, 
            @PathVariable String userId) {
        TrainingProgressDTO progress = progressService.getTrainingProgress(userId, trainingId);
        if (progress == null) {
            throw new ResourceNotFoundException("Progress not found for user " + userId + " and training " + trainingId);
        }
        return ResponseEntity.ok(progress);
    }
}
