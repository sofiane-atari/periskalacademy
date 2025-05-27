package periskal.academy.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.dto.ChapterDTO;
import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.service.ModuleService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModuleControllerTest {

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private ModuleController moduleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidChapterDTOAndModuleId_whenAddChapterToModule_thenReturnsCreatedResponseWithChapter() {
        // Arrange
        Long moduleId = 1L;
        ChapterDTO chapterDTO = new ChapterDTO();
        Chapter mockChapter = new Chapter();
        mockChapter.setId(10L);

        when(moduleService.addChapterToModule(chapterDTO, moduleId)).thenReturn(mockChapter);

        // Act
        ResponseEntity<Chapter> response = moduleController.addChapterToModule(moduleId, chapterDTO);

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCodeValue)
                .isEqualTo(201);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(Chapter::getId)
                .isEqualTo(10L);

        verify(moduleService).addChapterToModule(chapterDTO, moduleId);
    }

    @Test
    void givenServiceThrowsException_whenAddChapterToModule_thenPropagatesException() {
        // Arrange
        Long moduleId = 1L;
        ChapterDTO chapterDTO = new ChapterDTO();

        when(moduleService.addChapterToModule(chapterDTO, moduleId))
                .thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        assertThatThrownBy(() -> moduleController.addChapterToModule(moduleId, chapterDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Something went wrong");

        verify(moduleService).addChapterToModule(chapterDTO, moduleId);
    }
}
