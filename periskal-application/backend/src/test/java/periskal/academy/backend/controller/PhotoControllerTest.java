package periskal.academy.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.jpa.Photo;
import periskal.academy.backend.service.PhotoService;

@ExtendWith(MockitoExtension.class)
class PhotoControllerTest {

    @Mock
    private PhotoService photoService;

    @InjectMocks
    private PhotoController photoController;

    private List<Photo> photos;

    @BeforeEach
    void setUp() {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setImageData("http://example.com/photo.jpg");

        photos = new ArrayList<>();
        photos.add(photo);
    }

    @Test
    void givenPhotoId_whenDeleteTraining_thenReturnsSuccessMessage() {
        // Arrange
        Long photoId = 1L;
        doNothing().when(photoService).deletePhoto(photoId);

        // Act
        ResponseEntity<Map<String, String>> response = photoController.deleteTraining(photoId);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Photo deleted successfully");
        verify(photoService, times(1)).deletePhoto(photoId);
    }

    @Test
    void givenPhotos_whenAddPhotos_thenReturnsSuccessMessage() {
        // Arrange
        Long chapterId = 10L;
        doNothing().when(photoService).addPhotos(chapterId, photos);

        // Act
        ResponseEntity<Map<String, String>> response = photoController.addPhotos(chapterId, photos);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Photo(s) addedsuccessfully");
        verify(photoService, times(1)).addPhotos(chapterId, photos);
    }
}
