import { TrainingProgress } from "./progress.model";
import { Training } from "./training.model";

export interface TrainingWithProgress extends Training {
  progress?: TrainingProgress | null;
  loadingProgress?: boolean;
  progressError?: string;
}