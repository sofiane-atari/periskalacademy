package periskal.academy.backend.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import periskal.academy.backend.dto.ChapterDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.ChapterRepository;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.ModuleRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    void givenNonexistentModuleId_whenGetModuleById_thenThrowsResourceNotFoundException() {
        // Arrange
        Long nonexistentModuleId = 999L;
        when(moduleRepository.findById(nonexistentModuleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> moduleService.getModuleById(nonexistentModuleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Training not found with id: " + nonexistentModuleId);

        verify(moduleRepository, times(1)).findById(nonexistentModuleId);
    }

    @Test
    void givenExistingModuleId_whenGetModuleById_thenReturnExistingModule() {
        // Arrange
        Long existingModuleId = 1L;
        Module existingModule = new Module();
        existingModule.setId(existingModuleId);
        existingModule.setNameModuleEn("Test Module");
        when(moduleRepository.findById(existingModuleId)).thenReturn(Optional.of(existingModule));

        // Act
        Module retrievedModule = moduleService.getModuleById(existingModuleId);

        // Assert
        assertThat(retrievedModule).isNotNull();
        assertThat(retrievedModule.getId()).isEqualTo(existingModuleId);
        assertThat(retrievedModule.getNameModuleEn()).isEqualTo("Test Module");

        verify(moduleRepository, times(1)).findById(existingModuleId);
    }

    @Test
    void givenNonexistentModuleId_whenAddChapterToModule_thenThrowsResourceNotFoundException() {
        // Arrange
        Long nonexistentModuleId = 999L;
        ChapterDTO chapterDTO = new ChapterDTO();
        when(moduleRepository.findById(nonexistentModuleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> moduleService.addChapterToModule(chapterDTO, nonexistentModuleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Training not found with id: " + nonexistentModuleId);

        verify(moduleRepository, times(1)).findById(nonexistentModuleId);
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void givenValidModuleIdAndChapterDTO_whenAddChapterToModule_thenChapterIsSavedAndReturned() {
        // Arrange
        Long moduleId = 1L;
        Module existingModule = new Module();
        existingModule.setId(moduleId);
        ChapterDTO chapterDTO = new ChapterDTO();
        chapterDTO.setNameChapterEn("Test Chapter");
        Chapter savedChapter = new Chapter();
        savedChapter.setId(1L);
        savedChapter.setNameChapterEn("Test Chapter");
        savedChapter.setModule(existingModule);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(chapterRepository.save(any(Chapter.class))).thenReturn(savedChapter);

        // Act
        Chapter result = moduleService.addChapterToModule(chapterDTO, moduleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNameChapterEn()).isEqualTo("Test Chapter");
        assertThat(result.getModule()).isEqualTo(existingModule);

        verify(moduleRepository, times(1)).findById(moduleId);
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }

    @Test
    void givenValidModuleIdAndChapterDTO_whenAddChapterToModule_thenThrowsDataIntegrityViolationException() {
        // Arrange
        Long moduleId = 1L;
        Module existingModule = new Module();
        existingModule.setId(moduleId);
        ChapterDTO chapterDTO = new ChapterDTO();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(chapterRepository.save(any(Chapter.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThatThrownBy(() -> moduleService.addChapterToModule(chapterDTO, moduleId))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(moduleRepository, times(1)).findById(moduleId);
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }

    @Test
    void givenValidModuleIdAndChapterDTO_whenAddChapterToModule_thenThrowsRuntimeExceptionDueToDataAccessError() {
        // Arrange
        Long moduleId = 1L;
        Module existingModule = new Module();
        existingModule.setId(moduleId);
        ChapterDTO chapterDTO = new ChapterDTO();
        DataAccessResourceFailureException expectedException = new DataAccessResourceFailureException(
                "Simulated database error");

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(chapterRepository.save(any(Chapter.class))).thenThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> moduleService.addChapterToModule(chapterDTO, moduleId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to add chapter to module " + moduleId + ".")
                .hasCause(expectedException);

        verify(moduleRepository, times(1)).findById(moduleId);
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }
}