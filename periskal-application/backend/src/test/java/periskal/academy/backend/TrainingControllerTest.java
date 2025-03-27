package periskal.academy.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import periskal.academy.backend.controller.TrainingController;
import periskal.academy.backend.dto.ModuleDTO;
import periskal.academy.backend.dto.TrainingDTO;
import periskal.academy.backend.jpa.Module;
import periskal.academy.backend.jpa.Training;
import periskal.academy.backend.service.TrainingService;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    private Training training;
    private Module module;
    private ModuleDTO moduleDTO;
    private TrainingDTO trainingDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();

        training = new Training();
        training.setId(1L);
        training.setName_en("Test Training");

        module = new Module();
        module.setId(1L);
        module.setNameModule("Test Module");
        training.setModules(Collections.singletonList(module));

        moduleDTO = new ModuleDTO();
        moduleDTO.setNameModule("Test Module DTO");

        trainingDTO = new TrainingDTO();
        trainingDTO.setId(1L);
        trainingDTO.setName_en("Test Training DTO");
    }

    // GET /api/trainings
    @Test
    void getAllTrainings_ShouldReturnListOfTrainings() throws Exception {

        TrainingDTO dto = new TrainingDTO();
        dto.setId(1L);
        dto.setName_en("Test Training");

        when(trainingService.getAllTrainings()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/trainings")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L)) // Array van objecten
                .andExpect(jsonPath("$[0].name").value("Test Training"));
    }

    // GET /api/trainings/{trainingId}
    @Test
    void getTrainingById_ShouldReturnTraining() throws Exception {
        // 1. Arrange
        Training training = new Training();
        training.setId(1L);
        training.setName_en("Test Training");
        when(trainingService.getTrainingWithProducts(1L)).thenReturn(Optional.of(training));

        // 2. Act & Assert
        mockMvc.perform(get("/api/trainings/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Training"));

        // 3. Verify
        verify(trainingService, times(1)).getTrainingWithProducts(1L);
    }

    // GET /api/trainings/{trainingId}/chapters
    @Test
    void getModulesForTraining_ShouldReturnModules() throws Exception {
        when(trainingService.getTrainingWithProducts(1L)).thenReturn(Optional.of(training));

        mockMvc.perform(get("/api/trainings/1/chapters")
                .accept(MediaType.APPLICATION_JSON)) // Voeg deze header toe
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nameModule").value("Test Module"));

        verify(trainingService, times(1)).getTrainingWithProducts(1L);
    }

    // POST /api/trainings/create
    @Test
    void createTraining_ShouldReturnCreatedTraining() throws Exception {
        when(trainingService.createTraining(any(Training.class))).thenReturn(training);

        mockMvc.perform(post("/api/trainings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Training\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Training"));

        verify(trainingService, times(1)).createTraining(any(Training.class));
    }

    // PUT /api/trainings/edit/{id}
    @Test
    void updateTraining_ShouldReturnUpdatedTraining() throws Exception {
        when(trainingService.updateTraining(eq(1L), any(Training.class))).thenReturn(training);

        mockMvc.perform(put("/api/trainings/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Training\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Training"));

        verify(trainingService, times(1)).updateTraining(eq(1L), any(Training.class));
    }

    @Test
    void updateTraining_WhenServiceThrowsException_ShouldReturn500() throws Exception {
        when(trainingService.updateTraining(eq(1L), any(Training.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put("/api/trainings/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Training\"}"))
                .andExpect(status().isInternalServerError());

        verify(trainingService, times(1)).updateTraining(eq(1L), any(Training.class));
    }

    // DELETE /api/trainings/delete/{id}
    @Test
    void deleteTraining_ShouldReturnSuccessMessage() throws Exception {
        when(trainingService.deleteTraining(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/trainings/delete/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Training deleted successfully"));
    }

    @Test
    void deleteTraining_WhenNotFound_ShouldReturn404() throws Exception {
        when(trainingService.deleteTraining(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/trainings/delete/1"))
                .andExpect(status().isNotFound());

        verify(trainingService, times(1)).deleteTraining(1L);
    }

    // POST /api/trainings/{trainingId}/modules
    @Test
    void addModuleToTraining_ShouldReturnCreatedModule() throws Exception {
        when(trainingService.addModuleWithDto(eq(1L), any(ModuleDTO.class))).thenReturn(module);

        mockMvc.perform(post("/api/trainings/1/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"nameModule\":\"New Module\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nameModule").value("Test Module"));

        verify(trainingService, times(1)).addModuleWithDto(eq(1L), any(ModuleDTO.class));
    }

    // DELETE /api/trainings/delete/{trainingId}/modules/{moduleId}
    @Test
    void deleteModule_ShouldReturnUpdatedTraining() throws Exception {
        // Arrange
        Training training = new Training();
        training.setId(1L);
        when(trainingService.deleteModule(1L, 1L)).thenReturn(training);

        // Act & Assert
        mockMvc.perform(delete("/api/trainings/delete/1/modules/1")
                .accept(MediaType.APPLICATION_JSON)) // Voeg deze lijn toe
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        // Verify
        verify(trainingService, times(1)).deleteModule(1L, 1L);
    }

    @Test
    void deleteModule_WhenNotFound_ShouldReturn404() throws Exception {
        when(trainingService.deleteModule(1L, 1L)).thenReturn(null);

        mockMvc.perform(delete("/api/trainings/delete/1/modules/1"))
                .andExpect(status().isNotFound());

        verify(trainingService, times(1)).deleteModule(1L, 1L);
    }

    // PUT /api/trainings/{id}/modules
    @Test
    void updateModules_ShouldReturnUpdatedTraining() throws Exception {
        when(trainingService.updateModules(eq(1L), anyList())).thenReturn(training);

        mockMvc.perform(put("/api/trainings/1/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("[{\"id\":1,\"nameModule\":\"Updated Module\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(trainingService, times(1)).updateModules(eq(1L), anyList());
    }

    @Test
    void updateModules_WhenUpdateFails_ShouldReturn400() throws Exception {
        Training failedTraining = new Training();
        failedTraining.setModules(new ArrayList<>());

        when(trainingService.updateModules(eq(1L), anyList())).thenReturn(failedTraining);

        mockMvc.perform(put("/api/trainings/1/modules")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"id\":1,\"nameModule\":\"Updated Module\"}]"))
                .andExpect(status().isBadRequest());

        verify(trainingService, times(1)).updateModules(eq(1L), anyList());
    }
}