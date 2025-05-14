package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.dto.progress.ChapterCompletion;
import periskal.academy.backend.dto.progress.TrainingProgressDTO;
import periskal.academy.backend.service.ProgressService;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    
    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/complete-chapter")
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
    public ResponseEntity<List<TrainingProgressDTO>> getUserProgress(@PathVariable String userId) {
        List<TrainingProgressDTO> progress = progressService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/training/{trainingId}/user/{userId}")
    public ResponseEntity<TrainingProgressDTO> getTrainingProgress(
            @PathVariable Long trainingId, 
            @PathVariable String userId) {
        TrainingProgressDTO progress = progressService.getTrainingProgress(userId, trainingId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
