import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkipperService } from '../../services/Skippers/skipper.service';
import { Skipper } from '../../models/Skipper.model';
import { TrainingService } from '../../services/Training/training.service';
import { ProgressService } from '../../services/Progress/progress.service';
import { ModuleProgress } from '../../models/progress.model';
import { TrainingWithProgress } from '../../models/TrainingWithProgress.model';
import { SkipperDisplay } from '../../models/SkipperDisplay.model';
import { Training } from '../../models/training.model';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';


@Component({
  selector: 'app-skippers',
  standalone: true,
  imports: [CommonModule],
  providers: [],
  templateUrl: './skippers.component.html',
  styleUrls: ['./skippers.component.css']
})
export class SkippersComponent implements OnInit {
  skip: Skipper[] = [];
  skippers: SkipperDisplay[] = [];
  selectedSkipperIndex: number | null = null;
  error: string | null = null;

  constructor(
    private skipperService: SkipperService,
    private trainingService: TrainingService,
    private progressService: ProgressService,
  ) {}

  ngOnInit() {
    this.fetchSkippersData();
  }

  fetchSkippersData() {
    this.error = null;
    this.skipperService.getSkippers().subscribe({
      next: (data: Skipper[]) => {
        this.skippers = data.map(s => ({
          ...s,
          accessibleTrainings: [],
          loadingTrainingsAndProgress: false,
          trainingsAndProgressError: null,
        }));
      },
      error: (err: unknown) => {
        console.error('Error fetching skippers:', err);
        this.error = 'Failed to load skippers. Please try again later.';
      }
    });
  }

  toggleSkipperDetails(index: number) {
    if (this.selectedSkipperIndex === index) {
      this.selectedSkipperIndex = null;
    } else {
      this.selectedSkipperIndex = index;
      const skipper = this.skippers[index];
      // Load trainings and progress only if not already loaded and not currently loading
      if (skipper && skipper.accessibleTrainings.length === 0 && !skipper.loadingTrainingsAndProgress && !skipper.trainingsAndProgressError) {
        this.loadTrainingsAndProgressForSkipper(skipper);
      }
    }
  }

  loadTrainingsAndProgressForSkipper(skipper: SkipperDisplay) {
    if (!skipper.products || skipper.products.length === 0) {
      skipper.trainingsAndProgressError = 'Skipper has no products to determine trainings.';
      skipper.accessibleTrainings = [];
      return;
    }
    if (!skipper.role) {
      skipper.trainingsAndProgressError = 'Skipper role is missing, cannot filter trainings.';
      return;
    }

    skipper.loadingTrainingsAndProgress = true;
    skipper.trainingsAndProgressError = null;

    this.trainingService.getFilteredTrainingData(skipper.role, skipper.products).pipe(
      switchMap((trainings: Training[]) => {
        if (!trainings || trainings.length === 0) {
          skipper.accessibleTrainings = [];
          return of([]); // No trainings
        }

        const progressObservables = trainings.map(training => {
          const trainingItem: TrainingWithProgress = {
            ...training,
            loadingProgress: true,
            progress: null,
            progressError: undefined
          };


          const userIdForProgress = skipper.id;

          return this.progressService.getTrainingProgress(userIdForProgress, training.id).pipe(
            map(progressData => {
              trainingItem.progress = progressData || null;
              trainingItem.loadingProgress = false;
              return trainingItem;
            }),
            catchError(err => {
              console.error(`Error fetching progress for training ${training.id} (Skipper: ${userIdForProgress}):`, err);
              trainingItem.progressError = 'Progress unavailable';
              trainingItem.loadingProgress = false;
              trainingItem.progress = null;
              return of(trainingItem); // Return the item with error info
            })
          );
        });
        return forkJoin(progressObservables); // Wait for all fetches
      }),
      catchError(err => {
        console.error(`Error fetching trainings for skipper ${skipper.username}:`, err);
        skipper.trainingsAndProgressError = 'Failed to load trainings information.';
        skipper.accessibleTrainings = [];
        return of([]); // Return empty array 
      })
    ).subscribe({
      next: (trainingsWithProgress: TrainingWithProgress[]) => {
        skipper.accessibleTrainings = trainingsWithProgress;
        skipper.loadingTrainingsAndProgress = false;
      },
    });
  }
}
