import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { RoleAuthorizationService } from '../../../services/RoleAuthorization/role-authorization.service';
import { Training } from '../../../models/training.model';
import { Exam } from '../../../models/exam.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { catchError, of } from 'rxjs';
import { DisplayTraining } from '../../../models/display-training.model';
import { TrainingAssignment } from '../../../models/training-assignment.model';
import { TrainingAssignmentService } from '../../../services/Assignment/training-assignment.service';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-dashboard',
  imports: [TranslatePipe, DatePipe],
  templateUrl: './dashboard-employee.component.html',
  styleUrl: './dashboard-employee.component.css'
})

export class DashboardComponent implements OnInit {
  public trainingsData: Training[] = [];
  public lang: string | null = "";
  public openMenus: { [key: number]: boolean } = {};
  public menuOpenAdd: boolean = false;
  public examsMap: Map<number, Exam> = new Map();
  public isLoadingExams = false;
  public loadingError: string | null = null;

  constructor(
    private router: Router,
    private examService: ExamService,
    private trainingSvc: TrainingService,
    private jwtDecoderService: JwtDecoderService,
    public roleAuth: RoleAuthorizationService,
    private assignmentService: TrainingAssignmentService
  ) { }

  public allTrainingsData: DisplayTraining[] = [];
  public fleetManagerAssignedTrainings: DisplayTraining[] = [];
  private userRawAssignments: TrainingAssignment[] = [];
  public isLoadingData = true;

  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    this.loadUserAssignments();
  }

  // Step 1: Load User's Assignments 
  loadUserAssignments(): void {
    this.isLoadingData = true;
    this.loadingError = null;
    const skipperId = this.jwtDecoderService.getId();

    if (!skipperId) {
      this.loadingError = "User ID not found, cannot load assignments.";
      this.loadAllAvailableTrainings();
      return;
    }

    this.assignmentService.getSkipperAssignments(skipperId).pipe(
      catchError(err => {
        this.loadingError = "Failed to load your assignments.";
        return of([] as TrainingAssignment[]);
      })
    ).subscribe(assignments => {
      this.userRawAssignments = assignments;
      this.loadAllAvailableTrainings();
    });
  }

  // Step 2: load all available trainings
  loadAllAvailableTrainings(): void {

    const role = this.jwtDecoderService.getUserRole() || "noRole";
    const products: string[] = this.jwtDecoderService.getProducts() || [];

    this.trainingSvc.getFilteredTrainingData(role, products).subscribe({
      next: (data: Training[]) => {
        this.trainingsData = data;
        this.processLoadedData(this.trainingsData, this.userRawAssignments);
        this.loadExamsForTrainings();
        this.isLoadingData = false;
      }
    });
  }
  private sortByDate(a: DisplayTraining, b: DisplayTraining): number {
    const dateA = a.assignedDate ? new Date(a.assignedDate).getTime() : 0;
    const dateB = b.assignedDate ? new Date(b.assignedDate).getTime() : 0;
    return dateB - dateA; // Newest date comes first
  }

  // Step 3: Process and combine the data
  processLoadedData(availableTrainings: Training[], assignments: TrainingAssignment[]): void {

    const assignmentMap = new Map<number, TrainingAssignment>();
    assignments.forEach(assignment => {
      assignmentMap.set(assignment.trainingId, assignment);
    });

    // Populate fleetManagerAssignedTrainings (for the top list)
    this.fleetManagerAssignedTrainings = [];
    assignments.forEach(assignment => {
      const assignedTrainingDetails = availableTrainings.find(t => t.id === assignment.trainingId);
      if (assignedTrainingDetails) {
        this.fleetManagerAssignedTrainings.push({
          ...assignedTrainingDetails,
          isAssigned: true,
          assignedDate: assignment.assignedDate
        });
      }
    });
    // Populate allTrainingsData (for the main grid)
    this.allTrainingsData = availableTrainings.map(training => {
      const assignment = assignmentMap.get(training.id);
      return {
        ...training,
        isAssigned: !!assignment,
        assignedDate: assignment ? assignment.assignedDate : null
      };
    });

    this.allTrainingsData.sort((a, b) => {
      if (a.isAssigned && !b.isAssigned) return -1; // a comes first
      if (!a.isAssigned && b.isAssigned) return 1; // b comes first
      return this.sortByDate(a, b); // Sort by date within assigned/unassigned groups
    });

    // Sort fleetManagerAssignedTrainings by date
    this.fleetManagerAssignedTrainings.sort(this.sortByDate);
  }

  loadExamsForTrainings(): void {
    this.isLoadingExams = true;

    this.trainingsData.forEach(training => {
      this.examService.getExamByTrainingId(training.id).subscribe({
        next: (exam) => {
          if (exam) {
            this.examsMap.set(training.id, exam);
          }
        },
        complete: () => {
          this.isLoadingExams = false;
        }
      });
    });
  }

  hasExam(training: Training): boolean {
    const exam = this.examsMap.get(training.id);
    return !!exam;
  }

  goToExam(training: Training): void {
    const exam = training.exam
    if (exam) {
      this.router.navigate(['/take-exam', exam.id]);
    }
  }

  goToTrainingDetails(training: Training) {
    this.router.navigate(['/training', training.id]);
  }

  getTrainingName(training: Training) {
    if (this.lang == "en") {
      return training.nameEn;
    }
    if (this.lang == "nl") {
      return training.nameNl;
    }
    if (this.lang == "de") {
      return training.nameDe;
    }
    if (this.lang == "fr") {
      return training.nameFr
    } else {
      return "Unknown language"
    }
  }

  getAssignedTrainingName(training: Training) {
    if (!training) {
      return "Training Not Found";
    }

    if (this.lang === "en") {
      return training.nameEn;
    }
    if (this.lang === "nl") {
      return training.nameNl;
    }
    if (this.lang === "de") {
      return training.nameDe;
    }
    if (this.lang === "fr") {
      return training.nameFr;
    } else {
      return "Unknown language";
    }
  }

  getTrainingDescription(training: Training) {
    if (this.lang == "en") {
      return training.descriptionEn
    }
    if (this.lang == "nl") {
      return training.descriptionNl
    }
    if (this.lang == "de") {
      return training.descriptionDe
    }
    if (this.lang == "fr") {
      return training.descriptionFr
    } else {
      return "Unknown language"
    }
  }

  toggleMenuEdit(training: Training) {
    // Sluit alle andere open dropdowns
    Object.keys(this.openMenus).forEach(key => {
      this.openMenus[+key] = false;
    });
    // Toggle de huidige
    this.openMenus[training.id] = !this.openMenus[training.id];
  }

  toggleMenuAdd() {
    this.menuOpenAdd = !this.menuOpenAdd;
  }

  goToAddTrainingPage() {
    this.router.navigate(['/add-training'])
  }

  goToEditTrainingPage(training: Training) {
    this.router.navigate(['/edit', training.id])
  }

  goToAddExamPage() {
    this.router.navigate(['/add-exam'])
  }

  goToEditExamPage(training: Training) {
    const exam = this.examsMap.get(training.id);
    if (exam) {
      this.router.navigate(['/update-exam', exam.id]);
    }
  }

  getCountModules(training: Training): number {
    return this.trainingsData.find(t => t.id === training.id)?.modules?.length ?? 0;
  }

}



