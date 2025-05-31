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
import { CompletedExamService } from '../../services/CompletedExam/completed-exam.service';
import { CompletedExam } from '../../models/completed-exam.model';
import { CertificateService } from '../../services/Certificates/certificate.service';
import { Certificate } from '../../models/Certificate.model';
import { UserAttemptService } from '../../services/UserExamAttempts/user-attempt.service';
import { RoleAuthorizationService } from '../../services/RoleAuthorization/role-authorization.service';


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
  skippersWithActiveTraining: string[] = [];

  selectedTrainingId: number | null = null;
  selectedSkipperIds: string[] = [];
  availableTrainings: Training[] = [];
  assignmentDialogOpen: boolean = false;
  selectedSkipperExams: CompletedExam[] = [];
  loadingCompletedExams: boolean = false;
  completedExamsError: boolean = false;
  selectedSkipperCertificates: Certificate[] = [];
  loadingCertificates: boolean = false;
  certificatesError: boolean = false;

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
    examPrice: number;
  } | null = null;

  isProcessingAction: boolean = false;

  constructor(
    private skipperService: SkipperService,
    private trainingService: TrainingService,
    private progressService: ProgressService,
    private assignmentService: TrainingAssignmentService,
    private jwtDecoder: JwtDecoderService,
    private examService: ExamService,
    private snackBar: MatSnackBar,
    private completedExamService: CompletedExamService,
    private certificateService: CertificateService,
    private userAttemptService: UserAttemptService,
    public roleAuth: RoleAuthorizationService
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

        this.skippers.forEach(skipper => {
          this.loadTrainingsAndProgressForSkipper(skipper);
        });
      },
      error: (err: unknown) => {
        this.error = 'Failed to load skippers. Please try again later.';
      }
    });
  }

  onSkipperClick(skipperId: string) {
    this.loadingCompletedExams = true;
    this.loadingCertificates = true;
    this.completedExamService.getUserCompletedExams(skipperId).subscribe({
      next: (exams: CompletedExam[]) => {
        this.selectedSkipperExams = exams;
        this.loadingCompletedExams = false;
      },
      error: (err) => {
        this.completedExamsError = true;
        this.loadingCompletedExams = false;
      }
    });

    this.certificateService.getCertificatesByUserId(skipperId).subscribe({
      next: (certificates: Certificate[]) => {
        this.selectedSkipperCertificates = certificates;
        this.loadingCertificates = false;
      },
      error: (err) => {
        this.certificatesError = true;
        this.loadingCertificates = false;
      }
    })
  }

  getExamsGroupedByTrainingName() {
    const grouped = new Map<string, any[]>();
    for (const exam of this.selectedSkipperExams) {
      const training = exam.trainingName || 'Unknown Training';
      if (!grouped.has(training)) {
        grouped.set(training, []);
      }
      grouped.get(training)!.push(exam);
    }
    return Array.from(grouped.entries());
  }

  getCertificatesGroupedByTrainingName(): [string, any[]][] {
    const grouped: Record<string, any[]> = {};

    for (const cert of this.selectedSkipperCertificates || []) {
      const trainingName = cert.training || 'Unknown Training';
      if (!grouped[trainingName]) {
        grouped[trainingName] = [];
      }
      grouped[trainingName].push(cert);
    }

    return Object.entries(grouped);
  }



  toggleSkipperDetails(index: number) {
    if (this.selectedSkipperIndex === index) {
      this.selectedSkipperIndex = null;
    } else {
      this.selectedSkipperIndex = index;
      const skipper = this.skippers[index];
      const skipperId = this.skippers[index].id;
      this.onSkipperClick(skipperId);
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
      this.error = 'Failed to retrieve fleetmanager ID.';
      this.snackBar.open('Failed to retrieve fleetmanager ID.', 'Close', {
        duration: 5000,
        panelClass: ['snackbar-error']
      });
      return;
    }

    if (this.selectedTrainingId === null) {
      this.error = 'Please select a training to assign.';
      this.snackBar.open('Please select a training to assign.', 'Close', {
        duration: 5000,
        panelClass: ['snackbar-error']
      });
      return;
    }

    this.isProcessingAction = true;

    // Filter out skippers who already have active attempts
    const assignableSkipperIds = this.selectedSkipperIds.filter(
      skipperId => !this.skippersWithActiveTraining.includes(skipperId)
    );

    // If some skippers are already assigned, show a warning
    if (assignableSkipperIds.length < this.selectedSkipperIds.length) {
      const alreadyAssignedSkippers = this.selectedSkipperIds
        .filter(id => this.skippersWithActiveTraining.includes(id))
        .map(id => this.getSkipperName(id));
      const warningMessage = `The following skippers already have this training assigned and will be skipped: ${alreadyAssignedSkippers.join(', ')}.`;
      this.snackBar.open(warningMessage, 'Close', {
        duration: 7000,
        panelClass: ['snackbar-warning']
      });
    }

    // If no skippers are assignable, stop the process
    if (assignableSkipperIds.length === 0) {
      this.error = 'No assignable skippers selected.';
      this.snackBar.open('No assignable skippers selected.', 'Close', {
        duration: 5000,
        panelClass: ['snackbar-error']
      });
      this.isProcessingAction = false;
      return;
    }

    // Check if user is admin or support
    if (this.roleAuth.isAdmin() || this.roleAuth.isSupport()) {
        if (this.selectedTrainingId === null) {
            this.snackBar.open('Please select a training to assign.', 'Close', {
                duration: 5000,
                panelClass: ['snackbar-error']
            });
            this.isProcessingAction = false;
            return;
        }
        this.saveAssignment(assignableSkipperIds);
        this.isProcessingAction = false;
        return;
    }

    // For non-admin/support users, proceed with billing
    this.examService.getExamByTrainingId(this.selectedTrainingId).subscribe({
      next: (exam) => {
        if (exam && exam.id) {
          this.billingDataForForm = {
            userIds: assignableSkipperIds,
            examId: exam.id,
            examPrice: exam.price,
            examAttempts: exam.examAttempts
          };
          this.assignmentDialogOpen = false;
          this.showBillingModal = true;
          this.isProcessingAction = false;
        } else {
          // If no exam is associated, proceed without billing
          this.saveAssignment(assignableSkipperIds);
          this.isProcessingAction = false;
        }
      },
      error: (error) => {
        this.error = 'Failed to fetch exam information';
        this.snackBar.open('Failed to fetch exam information.', 'Close', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
        this.isProcessingAction = false;
      }
    });
  }

  saveAssignment(assignableSkipperIds: string[] = this.selectedSkipperIds) {
    this.showBillingModal = false;
    this.isProcessingAction = true;
    const fleetmanagerId = this.jwtDecoder.getId();

    if (!fleetmanagerId) {
        this.snackBar.open('Error: Unable to retrieve user ID. Please contact support.', 'Close', {
            duration: 5000,
            panelClass: ['snackbar-error']
        });
        this.isProcessingAction = false;
        return;
    }

    if (this.selectedTrainingId === null || assignableSkipperIds.length === 0) {
        this.snackBar.open('Critical error: Missing training or skipper data. Please retry.', 'Close', {
            duration: 5000,
            panelClass: ['snackbar-error']
        });
        this.isProcessingAction = false;
        return;
    }

    this.assignmentService.assignTrainingToSkippers(
        this.selectedTrainingId,
        assignableSkipperIds,
        fleetmanagerId
    ).subscribe({
        next: (response) => {
            this.assignmentDialogOpen = false;
            this.clearSkipperSelections();
            this.snackBar.open('Training successfully assigned!', 'Close', {
                duration: 3000,
                panelClass: ['snackbar-success']
            });
            this.fetchSkippersData();
            this.isProcessingAction = false;
            this.selectedSkipperIndex = null;
        },
        error: (error) => {
            this.error = 'Failed to assign training. Please try again later.';
            this.snackBar.open('CRITICAL: Training assignment failed. Please contact support.', 'Close', {
                duration: 7000,
                panelClass: ['snackbar-error']
            });
            this.isProcessingAction = false;
        }
    });
  }

  loadAvailableTrainings() {
    this.trainingService.getTrainingData().subscribe({
      next: (trainings) => {
        // Load all trainings without filtering based on remaining attempts
        this.availableTrainings = trainings;
        if (this.availableTrainings.length === 0) {
          this.snackBar.open('No trainings available to assign.', 'Close', {
            duration: 5000,
            panelClass: ['snackbar-info']
          });
        }
      },
      error: (err) => {
        this.error = 'Failed to load available trainings.';
        this.snackBar.open('Failed to load trainings.', 'Close', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

 checkSkippersWithActiveTraining(trainingId: number | null) {
    if (!trainingId || this.selectedSkipperIds.length === 0) {
        this.skippersWithActiveTraining = [];
        return;
    }

    setTimeout(() => { // Add 500ms delay to ensure backend transaction completes
        this.examService.getExamByTrainingId(trainingId).pipe(
            switchMap(exam => {
                if (!exam || !exam.id) {
                    this.snackBar.open('No exam associated with training ID: ' + trainingId, 'Close', {
                        duration: 5000,
                        panelClass: ['snackbar-warning']
                    });
                    this.skippersWithActiveTraining = [];
                    return of([]);
                }
                const examId = exam.id.toString();
                const attemptObservables = this.selectedSkipperIds.map(userId =>
                    this.userAttemptService.getRemainingAttempts(userId, examId).pipe(
                        map(response => {
                            if (response.remainingAttempts === 0) {
                                this.snackBar.open(`No exam attempts available for user ${userId}`, 'Close', {
                                    duration: 5000,
                                    panelClass: ['snackbar-warning']
                                });
                            }
                            return { userId, hasAttempts: response.remainingAttempts > 0 };
                        }),
                        catchError(err => {
                            this.snackBar.open(`Failed to check attempts for user ${userId}: ${err.message}`, 'Close', {
                                duration: 5000,
                                panelClass: ['snackbar-error']
                            });
                            return of({ userId, hasAttempts: false });
                        })
                    )
                );
                return forkJoin(attemptObservables);
            }),
            catchError(err => {
                this.snackBar.open('Failed to fetch exam for training ID: ' + trainingId, 'Close', {
                    duration: 5000,
                    panelClass: ['snackbar-error']
                });
                this.skippersWithActiveTraining = [];
                return of([]);
            })
        ).subscribe(results => {
            this.skippersWithActiveTraining = results
                .filter(result => result.hasAttempts)
                .map(result => result.userId);
        });
    }, 500);
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
    this.skippersWithActiveTraining = [];
    this.error = null;
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