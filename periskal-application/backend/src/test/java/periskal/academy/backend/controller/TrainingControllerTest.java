package periskal.academy.backend.controller;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.jpa.video.Video;
import periskal.academy.backend.service.FtpService;
import periskal.academy.backend.service.TrainingService;

import java.io.FileNotFoundException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private FtpService ftpService;

    @InjectMocks
    private TrainingController trainingController;

    private MultipartFile mockVideoFile;
    private Training training;
    private Module module;
    private List<TrainingDTO> trainingDTOs;
    private ModuleDTO moduleDTO;
    private Video video;

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

        video = new Video();
        video.setId(1L);
        video.setOriginalName("test-video.mp4");
        video.setFilePath("/videos/test-video.mp4");
    }

    @Test
    void givenTrainingsExist_whenGetAllTrainings_thenReturnsTrainings() {
        when(trainingService.getAllTrainings()).thenReturn(trainingDTOs);

        ResponseEntity<List<TrainingDTO>> response = trainingController.getAllTrainings();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(trainingDTOs);
    }

    @Test
    void givenValidTrainingId_whenGetProductsForTraining_thenReturnsTraining() {
        when(trainingService.getTrainingById(1L)).thenReturn(training);

        ResponseEntity<Training> response = trainingController.getProductsForTraining(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(training);
    }

    @Test
    void givenValidTrainingId_whenGetModuleWithChapters_thenReturnsModules() {
        training.setModules(Collections.singletonList(module));
        when(trainingService.getTrainingById(1L)).thenReturn(training);

        ResponseEntity<List<Module>> response = trainingController.getModuleWithChapters(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(module);
    }

    @Test
    void givenNewTraining_whenPostTrainingWithModules_thenReturnsCreatedTraining() {
        when(trainingService.createTraining(any(Training.class))).thenReturn(training);

        ResponseEntity<Training> response = trainingController.postTrainingWithModules(training);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(training);
    }

    @Test
    void givenValidTrainingId_whenUpdateTraining_thenReturnsUpdatedTraining() {
        Training updatedTraining = new Training();
        updatedTraining.setId(1L);
        updatedTraining.setNameEn("Updated Training");

        when(trainingService.updateTraining(1L, updatedTraining)).thenReturn(updatedTraining);

        ResponseEntity<Training> response = trainingController.updateTraining(1L, updatedTraining);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameEn()).isEqualTo("Updated Training");
    }

    @Test
    void givenValidTrainingId_whenDeleteTraining_thenReturnsSuccessMessage() {
        doNothing().when(trainingService).deleteTraining(1L);

        ResponseEntity<Map<String, String>> response = trainingController.deleteTraining(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Training deleted successfully");
    }

    @Test
    void givenValidTrainingIdAndModuleId_whenDeleteModule_thenReturnsUpdatedTraining() {
        when(trainingService.deleteModule(1L, 1L)).thenReturn(training);

        ResponseEntity<Training> response = trainingController.deleteModule(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(training);
    }

    @Test
    void givenValidRoleAndProducts_whenGetFilteredTrainings_thenReturnsFilteredList() {
        List<String> products = Arrays.asList("product1", "product2");
        when(trainingService.getFilteredTrainings("Skipper", products)).thenReturn(trainingDTOs);

        ResponseEntity<List<TrainingDTO>> response = trainingController.getFilteredTrainings("Skipper", products);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(trainingDTOs);
    }

    @Test
    void givenValidModules_whenUpdateModules_thenReturnsUpdatedTraining() {
        List<Module> modules = Collections.singletonList(module);
        training.setModules(modules);
        when(trainingService.updateModules(1L, modules)).thenReturn(training);

        ResponseEntity<Training> response = trainingController.updateModules(1L, modules);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getModules()).isEqualTo(modules);
    }

    @Test
    void givenValidTrainingIdAndModule_whenAddModuleToTraining_thenReturnsCreatedModule() {
        when(trainingService.addModuleWithDto(1L, moduleDTO)).thenReturn(module);

        ResponseEntity<Module> response = trainingController.addModuleToTraining(1L, moduleDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(module);
    }

    @Test
    void givenValidVideo_whenUploadVideo_thenReturnsVideo() throws Exception {
        when(trainingService.uploadVideo(mockVideoFile)).thenReturn(video);

        ResponseEntity<Video> response = trainingController.uploadVideo(mockVideoFile);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(video);
    }

    @Test
    void givenEmptyVideo_whenUploadVideo_thenReturnsBadRequest() {
        MultipartFile emptyFile = new MockMultipartFile("video", "", "video/mp4", new byte[0]);

        ResponseEntity<Video> response = trainingController.uploadVideo(emptyFile);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenExistingVideoPath_whenStreamVideo_thenReturnsVideoResource() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("video-content".getBytes());
        when(trainingService.streamVideoResource("/videos/test.mp4")).thenReturn(resource);

        HttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<Resource> response = trainingController.streamVideo("/videos/test.mp4", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(resource);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("video/mp4"));
    }

    @Test
    void givenNonExistentVideoPath_whenStreamVideo_thenReturnsNotFound() throws Exception {
        when(trainingService.streamVideoResource("/videos/nonexistent.mp4")).thenThrow(new FileNotFoundException());

        HttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<Resource> response = trainingController.streamVideo("/videos/nonexistent.mp4", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}