package periskal.academy.backend.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ChapterDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.ChapterRepository;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.security.Exceptions.General.ResourceNotFoundException;
import periskal.academy.backend.security.Exceptions.General.ValidationException;

@Slf4j
@Service
public class ModuleService {
    private ModuleRepository moduleRepository;
    private ChapterRepository chapterRepository;

    public ModuleService(ModuleRepository moduleRepository, ChapterRepository chapterRepository) {
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
    }

    public periskal.academy.backend.jpa.Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));
    }

    @Transactional
    public Chapter addChapterToModule(ChapterDTO chapterDTO, Long moduleId) {

        periskal.academy.backend.jpa.Module existingModule = getModuleById(moduleId);

        Chapter newChapter = new Chapter();
        newChapter.setNameChapter_en(chapterDTO.getNameChapter_en());
        newChapter.setDescriptionChapter_en(chapterDTO.getDescriptionChapter_en());
        newChapter.setNameChapter_nl(chapterDTO.getNameChapter_nl());
        newChapter.setDescriptionChapter_nl(chapterDTO.getDescriptionChapter_nl());
        newChapter.setNameChapter_fr(chapterDTO.getNameChapter_fr());
        newChapter.setDescriptionChapter_fr(chapterDTO.getDescriptionChapter_fr());
        newChapter.setNameChapter_de(chapterDTO.getNameChapter_de());
        newChapter.setDescriptionChapter_de(chapterDTO.getDescriptionChapter_de());
        newChapter.setPhotos_en(chapterDTO.getPhotos());
        newChapter.setModule(existingModule);

        try {
            Chapter savedChapter = chapterRepository.save(newChapter);
            log.info("Added new chapter {} to module {}", savedChapter.getId(), moduleId);
            return savedChapter;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to add module to training {} due to data integrity violation: {}", moduleId,
                    e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to add module to training {} due to database error: {}", moduleId, e.getMessage(), e);
            throw new RuntimeException("Failed to add module to training " + moduleId + ".", e);
        }
    }

}
