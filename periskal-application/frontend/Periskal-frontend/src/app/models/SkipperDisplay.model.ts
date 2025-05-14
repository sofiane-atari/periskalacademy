import { Skipper } from "./Skipper.model";
import { TrainingWithProgress } from "./TrainingWithProgress.model";

export interface SkipperDisplay extends Skipper {
  accessibleTrainings: TrainingWithProgress[];
  loadingTrainingsAndProgress: boolean;
  trainingsAndProgressError?: string | null;
}