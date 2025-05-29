package periskal.academy.backend.service;

import java.util.Date;
import java.util.List;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import periskal.academy.backend.dto.progress.ModuleProgressDTO;
import periskal.academy.backend.dto.progress.TrainingProgressDTO;
import periskal.academy.backend.jpa.progress.CompletedChapter;
import periskal.academy.backend.jpa.progress.CompletedChapterRepository;
import periskal.academy.backend.jpa.progress.ModuleProgress;
import periskal.academy.backend.jpa.progress.ModuleProgressRepository;
import periskal.academy.backend.jpa.progress.TrainingProgress;
import periskal.academy.backend.jpa.progress.TrainingProgressRepository;

@Service
public class ProgressService {

    private final TrainingProgressRepository trainingProgressRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final CompletedChapterRepository completedChapterRepository;
    private final ModuleService moduleService;

    public ProgressService(TrainingProgressRepository trainingProgressRepository,
            ModuleProgressRepository moduleProgressRepository,
            CompletedChapterRepository completedChapterRepository,
            ModuleService moduleService) {
        this.trainingProgressRepository = trainingProgressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.completedChapterRepository = completedChapterRepository;
        this.moduleService = moduleService;
    }

    public List<TrainingProgressDTO> getUserProgress(String userId) {
        List<TrainingProgress> progressEntities = trainingProgressRepository.findByUserId(userId);
        return progressEntities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public TrainingProgressDTO getTrainingProgress(String userId, Long trainingId) {
        return trainingProgressRepository.findByUserIdAndTrainingId(userId, trainingId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public TrainingProgressDTO completeChapter(String userId, Long trainingId, Long moduleId, Long chapterId) {
        // Get or create user progress for this training
        TrainingProgress userProgress = trainingProgressRepository.findByUserIdAndTrainingId(userId, trainingId)
                .orElseGet(() -> createNewUserProgress(userId, trainingId));

        // Get or create module progress
        ModuleProgress moduleProgress = getOrCreateModuleProgress(userProgress, moduleId);

        // Check if chapter is already completed
        if (!completedChapterRepository.existsByModuleProgressIdAndChapterId(moduleProgress.getId(), chapterId)) {
            // Mark chapter as completed
            CompletedChapter completedChapter = new CompletedChapter();
            completedChapter.setModuleProgress(moduleProgress);
            completedChapter.setChapterId(chapterId);
            completedChapter.setCompletedDate(new Date());
            completedChapterRepository.save(completedChapter);

            // Update percentages
            updateModulePercentage(moduleProgress);
            updateOverallPercentage(userProgress);
        }

        return convertToDTO(trainingProgressRepository.save(userProgress));
    }

    private TrainingProgress createNewUserProgress(String userId, Long trainingId) {
        TrainingProgress progress = new TrainingProgress();
        progress.setUserId(userId);
        progress.setTrainingId(trainingId);
        progress.setOverallPercentComplete(0.0);
        progress.setLastUpdated(new Date());
        return trainingProgressRepository.save(progress);
    }

    private ModuleProgress getOrCreateModuleProgress(TrainingProgress userProgress, Long moduleId) {
        return moduleProgressRepository.findByTrainingProgressIdAndModuleId(userProgress.getId(), moduleId)
                .orElseGet(() -> {
                    ModuleProgress moduleProgress = new ModuleProgress();
                    moduleProgress.setTrainingProgress(userProgress);
                    moduleProgress.setModuleId(moduleId);
                    moduleProgress.setPercentComplete(0.0);
                    userProgress.getModuleProgress().add(moduleProgress);
                    return moduleProgressRepository.save(moduleProgress);
                });
    }

    private void updateModulePercentage(ModuleProgress moduleProgress) {
        // Get total chapters in module
        int totalChapters = moduleService.getModuleById(moduleProgress.getModuleId()).getChapters().size();

        // Get completed chapters
        int completedCount = completedChapterRepository.findByModuleProgressId(moduleProgress.getId()).size();

        // Calculate percentage
        double percentage = (totalChapters > 0)
                ? ((double) completedCount / totalChapters) * 100.0
                : 0.0;

        moduleProgress.setPercentComplete(Math.rint(percentage));
        moduleProgressRepository.save(moduleProgress);
    }

    private void updateOverallPercentage(TrainingProgress trainingProgress) {
        // Calculate average of all module percentages
        double totalPercent = trainingProgress.getModuleProgress().stream()
                .mapToDouble(ModuleProgress::getPercentComplete)
                .average()
                .orElse(0.0);

        trainingProgress.setOverallPercentComplete(Math.rint(totalPercent));
        trainingProgress.setLastUpdated(new Date());
    }

    private TrainingProgressDTO convertToDTO(TrainingProgress entity) {
        TrainingProgressDTO dto = new TrainingProgressDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTrainingId(entity.getTrainingId());
        dto.setOverallPercentComplete(entity.getOverallPercentComplete());
        dto.setLastUpdated(entity.getLastUpdated());

        // Convert module progress
        List<ModuleProgressDTO> moduleDTOs = entity.getModuleProgress().stream()
                .map(this::convertToDTO)
                .toList();
        dto.setModuleProgress(moduleDTOs);

        return dto;
    }

    private ModuleProgressDTO convertToDTO(ModuleProgress entity) {
        ModuleProgressDTO dto = new ModuleProgressDTO();
        dto.setModuleId(entity.getModuleId());
        dto.setPercentComplete(entity.getPercentComplete());

        // Get chapter IDs from completedChapters
        List<Long> chapterIds = entity.getCompletedChapters().stream()
                .map(CompletedChapter::getChapterId)
                .toList();
        dto.setCompletedChapters(chapterIds);

        return dto;
    }
}
