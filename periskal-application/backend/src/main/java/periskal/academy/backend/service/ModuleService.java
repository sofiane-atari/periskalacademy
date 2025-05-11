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
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;

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
        newChapter.setNameChapterEn(chapterDTO.getNameChapterEn());
        newChapter.setDescriptionChapterEn(chapterDTO.getDescriptionChapterEn());
        newChapter.setTextChapterEn(chapterDTO.getTextChapterEn());
        newChapter.setNameChapterNl(chapterDTO.getNameChapterNl());
        newChapter.setDescriptionChapterNl(chapterDTO.getDescriptionChapterNl());
        newChapter.setTextChapterNl(chapterDTO.getTextChapterNl());
        newChapter.setNameChapterFr(chapterDTO.getNameChapterFr());
        newChapter.setDescriptionChapterFr(chapterDTO.getDescriptionChapterFr());
        newChapter.setTextChapterFr(chapterDTO.getTextChapterFr());
        newChapter.setNameChapterDe(chapterDTO.getNameChapterDe());
        newChapter.setDescriptionChapterDe(chapterDTO.getDescriptionChapterDe());
        newChapter.setTextChapterDe(chapterDTO.getTextChapterDe());
        newChapter.setPhotos(chapterDTO.getPhotos());
        newChapter.setModule(existingModule);

        try {
            Chapter savedChapter = chapterRepository.save(newChapter);
            log.info("Added new chapter {} to module {}", savedChapter.getId(), moduleId);
            return savedChapter;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to add chapter to module {} due to data integrity violation: {}", moduleId,
                    e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Failed to add chapter to module {} due to database error: {}", moduleId, e.getMessage(), e);
            throw new RuntimeException("Failed to add chapter to module " + moduleId + ".", e);
        }
    }

}
