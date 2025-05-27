package periskal.academy.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ChapterDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.service.ModuleService;
import periskal.academy.backend.security.SecurityRoles;

@Slf4j
@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/{moduleId}/chapters")
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Chapter> addChapterToModule(@PathVariable Long moduleId,
            @Valid @RequestBody ChapterDTO chapterDTO) {

        Chapter addedChapter = moduleService.addChapterToModule(chapterDTO, moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedChapter);

    }
}