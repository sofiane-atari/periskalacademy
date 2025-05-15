package periskal.academy.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.TrainingRepository;
import periskal.academy.backend.service.FtpService;
import periskal.academy.backend.service.TrainingService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @MockitoBean
    private TrainingRepository trainingRepository;

    @Mock
    private FtpService ftpService;

    private MultipartFile mockVideoFile;

    private Training training;
    private Module module;
    private List<TrainingDTO> trainingDTOs;
    private ModuleDTO moduleDTO;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setId(1L);
        training.setNameEn("Test Training");

        module = new Module();
        module.setId(1L);
        module.setNameModuleEn("Test Module");

        trainingDTOs = new ArrayList<>();
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setId(1L);
        trainingDTO.setNameEn("Test Training");
        trainingDTOs.add(trainingDTO);

        moduleDTO = new ModuleDTO();
        moduleDTO.setNameModuleEn("New Module");

        mockVideoFile = new MockMultipartFile(
                "video", "test-video.mp4", "video/mp4", "dummy-content".getBytes());
    }

    @Test
    void givenTrainingsExist_whenGetAllTrainings_thenReturnsTrainings() {
        // Arrange
        when(trainingService.getAllTrainings()).thenReturn(trainingDTOs);

        // Act
        ResponseEntity<List<TrainingDTO>> response = trainingController.getAllTrainings();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void givenValidTrainingId_whenGetProductsForTraining_thenReturnsTraining() {
        // Arrange
        when(trainingService.getTrainingById(1L)).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.getProductsForTraining(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void givenValidTrainingId_whenGetModuleWithChapters_thenReturnsModules() {
        // Arrange
        training.setModules(Collections.singletonList(module));
        when(trainingService.getTrainingById(1L)).thenReturn(training);

        // Act
        ResponseEntity<List<Module>> response = trainingController.getModuleWithChapters(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
    }

    @Test
    void givenNewTraining_whenPostTrainingWithModules_thenReturnsCreatedTraining() {
        // Arrange
        when(trainingService.createTraining(any(Training.class))).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.postTrainingWithModules(training);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void givenValidTrainingId_whenUpdateTraining_thenReturnsUpdatedTraining() {
        // Arrange
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setNameEn("Updated Training");

        when(trainingService.updateTraining(1L, updatedTraining)).thenReturn(updatedTraining);

        // Act
        ResponseEntity<Training> response = trainingController.updateTraining(1L, updatedTraining);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNameEn()).isEqualTo("Updated Training");
    }

    @Test
    void givenValidTrainingId_whenDeleteTraining_thenReturnsSuccessMessage() {
        // Arrange
        doNothing().when(trainingService).deleteTraining(1L);

        // Act
        ResponseEntity<Map<String, String>> response = trainingController.deleteTraining(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Training deleted successfully");
        verify(trainingService, times(1)).deleteTraining(1L);
    }

    @Test
    void givenValidTrainingIdAndModuleId_whenDeleteModule_thenReturnsUpdatedTraining() {
        // Arrange
        when(trainingService.deleteModule(1L, 1L)).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.deleteModule(1L, 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void givenValidRoleAndProducts_whenGetFilteredTrainings_thenReturnsFilteredList() {
        // Arrange
        List<String> products = Arrays.asList("product1", "product2");
        when(trainingService.getFilteredTrainings("Skipper", products)).thenReturn(trainingDTOs);

        // Act
        ResponseEntity<List<TrainingDTO>> response = trainingController.getFilteredTrainings("Skipper", products);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).isEqualTo(trainingDTOs);
    }

    @Test
    void givenValidModules_whenUpdateModules_thenReturnsUpdatedTraining() {
        // Arrange
        List<Module> modules = Collections.singletonList(module);
        training.setModules(modules);
        when(trainingService.updateModules(1L, modules)).thenReturn(training);

        // Act
        ResponseEntity<Training> response = trainingController.updateModules(1L, modules);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getModules()).isEqualTo(modules);
    }

    @Test
    void givenValidTrainingIdAndModule_whenAddModuleToTraining_thenReturnsCreatedModule() {
        // Arrange
        when(trainingService.addModuleWithDto(1L, moduleDTO)).thenReturn(module);

        // Act
        ResponseEntity<Module> response = trainingController.addModuleToTraining(1L, moduleDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNameModuleEn()).isEqualTo("Test Module");
    }

    @Test
    void givenValidVideo_WhenUploadVideoFtp_ThenReturnOkResponse() throws Exception {
        // Arrange
        URI mockUri = new URI("ftp://mockserver/videos/test-video.mp4");
        CompletableFuture<URI> futureUri = CompletableFuture.completedFuture(mockUri);

        when(ftpService.uploadFile(mockVideoFile, "test-video.mp4")).thenReturn(futureUri);

        // Act
        ResponseEntity<Map<String, String>> response = trainingController.uploadVideoFtp(mockVideoFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("status", "uploaded");
        assertThat(response.getBody()).containsEntry("filePath", "/videos/test-video.mp4");
    }

    @Test
    void givenEmptyVideo_WhenUploadVideoFtp_ThenReturnBadRequest() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile("video", "", "video/mp4", new byte[0]);

        // Act
        ResponseEntity<Map<String, String>> response = trainingController.uploadVideoFtp(emptyFile);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void givenExistingVideoPath_WhenStreamVideo_ThenReturnVideoResource() throws Exception {
        // Arrange
        String videoPath = "videos/test.mp4";
        byte[] content = "video-bytes".getBytes();
        ByteArrayResource resource = new ByteArrayResource(content);

        when(ftpService.retrieveFileAsResource(videoPath)).thenReturn(resource);

        HttpServletRequest request = new MockHttpServletRequest();

        // Act
        ResponseEntity<Resource> response = trainingController.streamVideo(videoPath, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().contentLength()).isEqualTo(content.length);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("video/mp4"));
    }

    @Test
    void givenNonExistentVideoPath_WhenStreamVideo_ThenReturnNotFound() throws Exception {
        // Arrange
        String videoPath = "non-existent.mp4";
        when(ftpService.retrieveFileAsResource(videoPath)).thenThrow(new FileNotFoundException());

        HttpServletRequest request = new MockHttpServletRequest();

        // Act
        ResponseEntity<Resource> response = trainingController.streamVideo(videoPath, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenIOExceptionDuringStream_WhenStreamVideo_ThenReturnInternalServerError() throws Exception {
        // Arrange
        String videoPath = "videos/test.mp4";
        when(ftpService.retrieveFileAsResource(videoPath)).thenThrow(new IOException("Error"));

        HttpServletRequest request = new MockHttpServletRequest();

        // Act
        ResponseEntity<Resource> response = trainingController.streamVideo(videoPath, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
