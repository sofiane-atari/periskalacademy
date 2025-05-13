package periskal.academy.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.jpa.PhotoRepository;
import periskal.academy.backend.jpa.ChapterRepository;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private PhotoService photoService;

    private Chapter chapter;
    private Photo photo;

    @BeforeEach
    void setUp() {
        chapter = new Chapter();
        chapter.setId(1L);
        chapter.setNameChapterEn("Test Chapter");

        photo = new Photo();
        photo.setId(1L);
        photo.setImageData("123456789123456789123456789123456789");
    }

    @Test
    void givenValidPhotoId_whenDeletePhoto_thenDeletesPhoto() {
        // Arrange
        when(photoRepository.existsById(1L)).thenReturn(true);

        // Act
        photoService.deletePhoto(1L);

        // Assert
        verify(photoRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNonExistentPhotoId_whenDeletePhoto_thenThrowsResourceNotFoundException() {
        // Arrange
        when(photoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> photoService.deletePhoto(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cannot delete. Photo not found with id: 1");
    }

    @Test
    void givenPhotoId_whenDeletePhotoWithDataIntegrityViolation_thenThrowsValidationException() {
        // Arrange
        when(photoRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("Data integrity violation")).when(photoRepository).deleteById(1L);

        // Act & Assert
        assertThatThrownBy(() -> photoService.deletePhoto(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot delete photo 1. It might be referenced by other data.");
    }

    @Test
    void givenChapterIdAndPhotos_whenAddPhotos_thenAddsPhotosToChapter() {
        // Arrange
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));

        // Act
        photoService.addPhotos(1L, Arrays.asList(photo));

        // Assert
        assertThat(photo.getChapter()).isEqualTo(chapter);
        verify(photoRepository, times(1)).saveAll(any());
    }

    @Test
    void givenInvalidChapterId_whenAddPhotos_thenDoesNotAddPhotos() {
        // Arrange
        when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        photoService.addPhotos(1L, Arrays.asList(photo));

        // Assert
        verify(photoRepository, never()).saveAll(any());
    }
}
