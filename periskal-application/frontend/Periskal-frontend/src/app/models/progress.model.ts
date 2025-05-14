export interface TrainingProgress {
  id: number;
  userId: string;
  trainingId: number;
  moduleProgress: ModuleProgress[];
  overallPercentComplete: number;
  lastUpdated: Date;
}

export interface ModuleProgress {
  moduleId: number;
  percentComplete: number;
  completedChapters: number[];
}

export interface CompletedChapter {
  chapterId: number;
  completedDate: Date;
}