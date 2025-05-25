import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkipperService } from '../../services/Skippers/skipper.service';
import { Skipper } from '../../models/Skipper.model';
import { TrainingService } from '../../services/Training/training.service';
import { ProgressService } from '../../services/Progress/progress.service';
import { TrainingWithProgress } from '../../models/TrainingWithProgress.model';
import { SkipperDisplay } from '../../models/SkipperDisplay.model';
import { Training } from '../../models/training.model';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { TrainingAssignmentService } from '../../services/Assignment/training-assignment.service';
import { FormsModule } from '@angular/forms';
import { ExamService } from '../../services/Exam/exam.service';
import { MatSnackBar, MatSnackBarConfig, MatSnackBarModule } from '@angular/material/snack-bar';
import { FleetManagerBillingComponent } from '../billing-fleetmanager/billing-fleetmanager.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ProgressBarComponent } from "../progress-bar/progress-bar.component";


@Component({
  selector: 'app-skippers',
  standalone: true,
  imports: [CommonModule, FormsModule, FleetManagerBillingComponent, MatSnackBarModule, TranslatePipe, ProgressBarComponent],
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

  searchTerm = '';
  selectedProduct = '';
  selectedShipname = '';
  currentPage = 1;
  itemsPerPage = 10;

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
  ) { }

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
          overallTrainingProgress: 0,
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
      } else if (skipper) {
        this.calculateOverallProgressForSkipper(skipper);
      }
    }
  }

  loadTrainingsAndProgressForSkipper(skipper: SkipperDisplay) {
    if (!skipper.products || skipper.products.length === 0) {
      skipper.trainingsAndProgressError = 'Skipper has no products to determine trainings.';
      skipper.accessibleTrainings = [];
      skipper.overallTrainingProgress = 0;
      return;
    }
    if (!skipper.role) {
      skipper.trainingsAndProgressError = 'Skipper role is missing, cannot filter trainings.';
      skipper.accessibleTrainings = [];
      skipper.overallTrainingProgress = 0;
      return;
    }

    skipper.loadingTrainingsAndProgress = true;
    skipper.trainingsAndProgressError = null;

    this.trainingService.getFilteredTrainingData(skipper.role, skipper.products).pipe(
      switchMap((trainings: Training[]) => {
        if (!trainings || trainings.length === 0) {
          skipper.accessibleTrainings = [];
          skipper.overallTrainingProgress = 0;
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
              return of(trainingItem);
            })
          );
        });
        return forkJoin(progressObservables);
      }),
      catchError(err => {
        console.error(`Error fetching trainings for skipper ${skipper.username}:`, err);
        skipper.trainingsAndProgressError = 'Failed to load trainings information.';
        skipper.accessibleTrainings = [];
        skipper.overallTrainingProgress = 0;
        return of([]);
      })
    ).subscribe({
      next: (trainingsWithProgress: TrainingWithProgress[]) => {
        skipper.accessibleTrainings = trainingsWithProgress;
        skipper.loadingTrainingsAndProgress = false;
        this.calculateOverallProgressForSkipper(skipper);
      },
    });
  }

  calculateOverallProgressForSkipper(skipper: SkipperDisplay): void {
    if (skipper.accessibleTrainings && skipper.accessibleTrainings.length > 0) {
      const totalTrainings = skipper.accessibleTrainings.length;

      const totalPercentage = skipper.accessibleTrainings.reduce((sum, training) => {
        return sum + (training.progress?.overallPercentComplete || 0);
      }, 0);

      skipper.overallTrainingProgress = totalTrainings > 0 ? Math.round(totalPercentage / totalTrainings) : 0;

    } else {
      skipper.overallTrainingProgress = 0;
    }
  }

  // Assignment Methods

  InitiateBillingAndAssignment() {
    const fleetmanagerId = this.jwtDecoder.getId();

    if (fleetmanagerId === null) {
      console.error('Fleetmanager ID is null');
      this.error = 'Failed to retrieve fleetmanager ID.';
      return;
    }

    if (this.selectedTrainingId === null) {
      console.error('No training selected');
      this.error = 'Please select a training to assign.';
      return;
    }

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

        } else {
          console.warn('No exam found for this training. Proceeding without billing details.');
          this.saveAssignment(); // Directly save assignment without billing modal
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
        this.clearSkipperSelections();
        this.snackBar.open('Training successfully assigned!', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });
        this.fetchSkippersData();
        this.isProcessingAction = false;
        this.selectedSkipperIndex = null;
      },
      error: (error) => {
        console.error('Error during assignment after Billing:', error);
        this.error = 'Failed to assign training. Please try again later.';
        this.snackBar.open('CRITICAL: Billing was successful, but training assignment failed. Please contact support.', 'Close', {
          duration: 7000,
          panelClass: ['snackbar-error']
        });
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
    this.selectedTrainingId = null;
    this.billingDataForForm = null;
    this.isProcessingAction = false;
  }

  handleBillingCancel() {
    this.showBillingModal = false;
    this.isProcessingAction = false;
    this.snackBar.open('Billing cancelled. Training assignment aborted.', 'Close', { duration: 3000 });
    this.assignmentDialogOpen = true;
  }

  getFilteredSkippers() {
    const searchTerm = this.searchTerm.toLowerCase();
    return this.skippers.filter(skipper => {
      const matchesSearch =
        !searchTerm ||
        skipper.lastname.toLowerCase().includes(searchTerm) ||
        skipper.shipname.toLowerCase().includes(searchTerm) ||
        skipper.products.some(product => product.toLowerCase().includes(searchTerm)) ||
        skipper.id.toLowerCase().includes(searchTerm) ||
        skipper.username.toLowerCase().includes(searchTerm) ||
        (skipper.firstname && skipper.firstname.toLowerCase().includes(searchTerm));

      const matchesProductFilter =
        !this.selectedProduct || skipper.products.includes(this.selectedProduct);

      const matchesShipFilter =
        !this.selectedShipname || skipper.shipname.includes(this.selectedShipname);

      return matchesProductFilter && matchesShipFilter && matchesSearch;
    })
  }

  getAllUniqueProducts(): string[] {
    const allProducts = this.skippers.flatMap(skipper => skipper.products);
    return [...new Set(allProducts)];
  }

  getAllUniqueShips(): string[] {
    const allShips = this.skippers.map(skipper => skipper.shipname);
    return [...new Set(allShips)];
  }

  getPaginatedSkippers() {
    const filtered = this.getFilteredSkippers();
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.getFilteredSkippers().length / this.itemsPerPage);
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  onFilterChange() {
    this.currentPage = 1;
  }

  onPageSizeChange() {
    this.currentPage = 1;
  }

  addAllToSkipperSelection(): void {
    const filteredSkippers = this.getFilteredSkippers();

    const selectedSkipperIdsSet = new Set<string>(this.mainListSelectedSkipperIds);

    let skippersAddedCount = 0;

    filteredSkippers.forEach(skipper => {
      if (!selectedSkipperIdsSet.has(skipper.id)) {
        selectedSkipperIdsSet.add(skipper.id);
        skippersAddedCount++;
      }
    });

    this.mainListSelectedSkipperIds = Array.from(selectedSkipperIdsSet);

    if (skippersAddedCount > 0) {
      const successSnackBarConfig: MatSnackBarConfig = {
        duration: 3000,
        verticalPosition: 'bottom',
        horizontalPosition: 'left',
        panelClass: ['snackbar-success'],
        politeness: 'assertive'
      };
      this.snackBar.open(`${skippersAddedCount} new skippers added to selection.`, 'Close', successSnackBarConfig);
    } else {
      const infoSnackBarConfig: MatSnackBarConfig = {
        duration: 5000,
        verticalPosition: 'bottom',
        horizontalPosition: 'left',
        panelClass: ['snackbar-info'],
        politeness: 'assertive'
      };
      this.snackBar.open('All relevant skippers are already selected.', 'Close', infoSnackBarConfig);
    }
  }

  getPageNumbers(): number[] {
    const pageNumbers: number[] = [];
    const totalPages = this.totalPages;
    const maxPagesToShow = 5;

    if (totalPages <= maxPagesToShow) {
      for (let i = 1; i <= totalPages; i++) {
        pageNumbers.push(i);
      }
    } else {
      let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
      let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

      if (endPage - startPage + 1 < maxPagesToShow) {
        startPage = Math.max(1, endPage - maxPagesToShow + 1);
      }

      for (let i = startPage; i <= endPage; i++) {
        pageNumbers.push(i);
      }
    }
    return pageNumbers;
  }

  goToPage(index: number) {
    this.currentPage = index;
  }
}