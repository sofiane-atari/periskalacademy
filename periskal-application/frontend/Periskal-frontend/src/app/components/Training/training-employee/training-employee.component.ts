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


@Component({
  selector: 'app-training-employee',
  imports: [TranslatePipe],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})

export class TrainingEmployeeComponent implements OnInit {
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
    public roleAuth: RoleAuthorizationService
  ) { }


  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    this.loadTrainings();
  }
  loadTrainings(): void {
    const role = this.jwtDecoderService.getUserRole() || "noRole";
    const products: string[] = this.jwtDecoderService.getProducts() || [];

    this.trainingSvc.getFilteredTrainingData(role, products).subscribe({
      next: (data: Training[]) => {
        this.trainingsData = data;
        this.loadExamsForTrainings();
      },
      error: (error) => {
        console.error('Error fetching trainings', error);
      }
    });
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
        error: (error) => {
          if (error.status !== 404) { // Only log non-404 errors
            console.error(`Error loading exam for training ${training.id}`, error);
          }
        },
        complete: () => {
          this.isLoadingExams = false;
        }
      });
    });
  }

  hasExam(trainingId: number): boolean {
    return this.examsMap.has(trainingId);
  }

  goToExam(trainingId: number): void {
    const exam = this.examsMap.get(trainingId);
    if (exam) {
      this.router.navigate(['/take-exam', exam.id]);
    }
  }
  goToTrainingDetails(trainingId: number) {
    this.router.navigate(['/training', trainingId]);
  }

  getTrainingName(index: number) {
    if (this.lang == "en") {
      return this.trainingsData[index].name_en
    }
    if (this.lang == "nl") {
      return this.trainingsData[index].name_nl
    }
    if (this.lang == "de") {
      return this.trainingsData[index].name_de
    }
    if (this.lang == "fr") {
      return this.trainingsData[index].name_fr
    } else {
      return "Unknown language"
    }
  }

  getTrainingDescription(index: number) {
    if (this.lang == "en") {
      return this.trainingsData[index].description_en
    }
    if (this.lang == "nl") {
      return this.trainingsData[index].description_nl
    }
    if (this.lang == "de") {
      return this.trainingsData[index].description_de
    }
    if (this.lang == "fr") {
      return this.trainingsData[index].description_fr
    } else {
      return "Unknown language"
    }
  }
  toggleMenuEdit(trainingId: number) {
    // Sluit alle andere open dropdowns
    Object.keys(this.openMenus).forEach(key => {
      this.openMenus[+key] = false;
    });
    // Toggle de huidige
    this.openMenus[trainingId] = !this.openMenus[trainingId];
  }
  
  toggleMenuAdd() {
    this.menuOpenAdd = !this.menuOpenAdd;
  }

  goToAddTrainingPage() {
    this.router.navigate(['/add-training'])
  }
  goToEditTrainingPage(trainingId: number) {
    this.router.navigate(['/edit', trainingId])
  }
  goToAddExamPage() {
    this.router.navigate(['/add-exam'])
  }
  goToEditExamPage(trainingId: number) {
    const exam = this.examsMap.get(trainingId);
    if (exam) {
      this.router.navigate(['/update-exam', exam.id]);
    }
  }
  getCountModules(trainingId: number) {
    const training = this.trainingsData.find(t => t.id === trainingId);
    return training?.modules?.length || 0;
  }
}



