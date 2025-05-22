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
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { TrainingAssignmentService } from '../../services/Assignment/training-assignment.service';
import { FormsModule } from '@angular/forms';
import { ExamService } from '../../services/Exam/exam.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FleetManagerBillingComponent } from '../billing-fleetmanager/billing-fleetmanager.component';


  @Component({
    selector: 'app-skippers',
    standalone: true,
    imports: [CommonModule, FormsModule, FleetManagerBillingComponent, MatSnackBarModule],
    providers: [],
    templateUrl: './skippers.component.html',
    styleUrls: ['./skippers.component.css']
  })
  export class SkippersComponent implements OnInit {
    skippers: SkipperDisplay[] = [];
    selectedSkipperIndex: number | null = null;
    error: string | null = null;

    selectedTrainingId: number | null = null;
    selectedSkipperIds: string[] = [];
    availableTrainings: Training[] = [];
    assignmentDialogOpen: boolean = false;

    mainListSelectedSkipperIds: string[] = [];

    showBillingModal: boolean = false;
    billingDataForForm: {
    userIds: string[];
    examId: number;
    examAttempts: number;
    } | null = null;

    isProcessingAction: boolean = false;

    constructor(
      private skipperService: SkipperService,
      private trainingService: TrainingService,
      private progressService: ProgressService,
      private assignmentService: TrainingAssignmentService,
      private jwtDecoder: JwtDecoderService,
      private examService: ExamService,
      private snackBar: MatSnackBar  
    ) {}

    ngOnInit() {  
      this.fetchSkippersData();
      this.mainListSelectedSkipperIds = [];
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

    // Assignment Methods

    InitiateBillingAndAssignment()
    {
      const fleetmanagerId = this.jwtDecoder.getId();

      if (fleetmanagerId === null) {
        console.error('Fleetmanager ID is null');
        this.error = 'Failed to retrieve fleetmanager ID.';
        return;}

      if(this.selectedTrainingId === null) {
        console.error('No training selected'); 
        this.error = 'Please select a training to assign.';
        return; }

        this.isProcessingAction = true;

        // Step 1: Fetch examAttempts for the selected trainingId
        this.examService.getExamByTrainingId(this.selectedTrainingId).subscribe({
          next: (exam) => {
            if (exam) {
              if (!exam.id) {
                throw new Error('Exam ID is undefined');
              }
              this.billingDataForForm = {
                userIds: this.selectedSkipperIds,
                examId: exam.id,
                examAttempts: exam.examAttempts
              };


              this.assignmentDialogOpen = false;
              this.showBillingModal = true;
              this.isProcessingAction = false;

            }
          },
          error: (error) => {
            console.error('Error fetching exam:', error);
            this.error = 'Failed to fetch exam information';
            this.isProcessingAction = false;
          }
        });
    }

    saveAssignment() {

      this.showBillingModal = false;
      this.isProcessingAction = true;
      const fleetmanagerId = this.jwtDecoder.getId();

      if (!fleetmanagerId || this.selectedTrainingId === null || this.selectedSkipperIds.length === 0) {
        this.snackBar.open('Critical error: Missing data for final assignment. Please retry.', 'Close', { duration: 5000, panelClass: ['snackbar-error'] });
        this.isProcessingAction = false;
        return;
    }

      this.assignmentService.assignTrainingToSkippers(
        this.selectedTrainingId,
        this.selectedSkipperIds,
        fleetmanagerId
      ).subscribe({
        next: (response) => {
          console.log('Assignment successful:', response);
          this.assignmentDialogOpen = false;
          this.fetchSkippersData(); // Refresh skippers data after assignment
        },
        error: (error) => {
          console.error('Error during assignment after Billing:', error);
          this.error = 'Failed to assign training. Please try again later.';
          this.snackBar.open('CRITICAL: Billing was successful, but training assignment failed. Please contact support.', 'Close', {
          duration: 7000,
          panelClass: ['snackbar-error']});
          this.isProcessingAction = false;
        }
      });
    }

    loadAvailableTrainings() { // TODO add a way to only load trainings that are ready, Add a Active button to the training that admins can click to set the training to active or inactive
                              // Create a new endpoint in the backend to get all trainings that are active and then use that here.

      this.trainingService.getTrainingData().subscribe({
        next: (trainings) => {
          this.availableTrainings = trainings;
        },
        error: (err) => {
          console.error('Error loading trainings:', err);
        }
      });
    }

    openAssignmentDialog() {

      this.selectedSkipperIds = [...this.mainListSelectedSkipperIds]; 
      if (this.selectedSkipperIds.length === 0) {
          alert("Please select at least one skipper from the list to assign training.");
          return;
      }

      this.loadAvailableTrainings();
      this.assignmentDialogOpen = true;
      this.selectedTrainingId = null;
    }

    getSkipperName(id: string): string {
    const skipper = this.skippers.find(s => s.id === id);

    if (skipper) {

      return `${skipper.username} (${skipper.firstname} ${skipper.lastname})`;

    }

    return `Skipper ID: ${id}`; // Fallback if skipper not found 
    }

    toggleSkipperSelection(skipperId: string, event: Event) {
      const isChecked = (event.target as HTMLInputElement).checked;
      if (isChecked) {
        if (!this.mainListSelectedSkipperIds.includes(skipperId)) {
          this.mainListSelectedSkipperIds.push(skipperId);
        }
      } else {
        this.mainListSelectedSkipperIds = this.mainListSelectedSkipperIds.filter(id => id !== skipperId);
      }
    }

    isSkipperSelected(skipperId: string): boolean {
      return this.mainListSelectedSkipperIds.includes(skipperId);
    }

    clearSkipperSelections() {
      this.mainListSelectedSkipperIds = [];
    }

    closeAssignmentDialog() {
      this.assignmentDialogOpen = false;
    }

    handleBillingCancel() {
    this.showBillingModal = false;
    this.isProcessingAction = false;
    this.snackBar.open('Billing cancelled. Training assignment aborted.', 'Close', { duration: 3000 });
    this.assignmentDialogOpen = true; // If you want to go back to assignment selection
  }

  }