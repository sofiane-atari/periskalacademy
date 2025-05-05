package periskal.academy.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ChapterDTO;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.ModuleService;
import periskal.academy.backend.service.TrainingService;

@Slf4j
@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/{moduleId}/chapters")
    public ResponseEntity<Chapter> addChapterToModule(@PathVariable Long moduleId,
            @Valid @RequestBody ChapterDTO chapterDTO) {

        Chapter addedChapter = moduleService.addChapterToModule(chapterDTO, moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedChapter);

    }
}