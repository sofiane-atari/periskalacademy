import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { Module } from '../../../models/module.model';
import { ProgressService } from '../../../services/Progress/progress.service';
import { TrainingProgress } from '../../../models/progress.model';
import { ProgressBarComponent } from '../../progress-bar/progress-bar.component';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { UserAttemptService } from '../../../services/UserExamAttempts/user-attempt.service';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe, ProgressBarComponent],
  templateUrl: './training-detail.component.html',
  styleUrl: './training-detail.component.css'
})
export class TrainingDetailComponent {
  training: Training | undefined;
  modules: Module[] = [];
  public lang: String | null = "";
  private userId: string = '';
  trainingId: number = 0;



  trainingProgress: TrainingProgress | undefined;

  constructor(
    private route: ActivatedRoute,
    private trainingSvc: TrainingService,
    private router: Router,
    private ProgressService: ProgressService,
    private jwtDecoderService: JwtDecoderService,
    private userAttemptSvc: UserAttemptService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {

    // Get the user ID from the JWT token
    this.userId = this.jwtDecoderService.getId() ?? 'Error'; //Change this to thow an error or something else later

    this.lang = localStorage.getItem('lang');
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    this.trainingSvc.getTrainingById(this.trainingId).subscribe({
      next: (data) => {
        this.training = data;
        this.modules = data.modules;
      },
      error: (error) => {
        console.error('Error fetching training products', error);
      }
    });

    this.loadProgressData();
  }

  goBack() {
    this.router.navigate(['trainingEmployee'])
  }

  startExam() {
    if (this.training?.exam?.id) {
      this.userAttemptSvc.startExam(this.training.exam.id, this.userId).subscribe({
        next: (response) => {
          console.log('Exam started successfully:', response);
          this.router.navigate(['/take-exam', this.training?.exam?.id]);
        },
        error: (err) => {
          console.error(err);
          this.goToBilling();
        }
      });
    }
  }

  goToBilling(): void {
    this.router.navigate(['/billing', this.training?.exam?.id]);
  }

  goToChaptersPage(moduleId: number) {
    this.router.navigate(['/training', this.trainingId, 'module', moduleId])
  }

  getModuleTitle(moduleId: number) {
    if (this.lang == "en") {
      return this.training?.modules[moduleId].nameModuleEn;
    }
    if (this.lang == "nl") {
      return this.training?.modules[moduleId].nameModuleNl;
    }
    if (this.lang == "fr") {
      return this.training?.modules[moduleId].nameModuleFr;
    }
    if (this.lang == "de") {
      return this.training?.modules[moduleId].nameModuleDe;
    }
    else {
      return "Unknown language"
    }
  }

  getModuleDescription(moduleId: number) {
    if (this.lang == "en") {
      return this.training?.modules[moduleId].descriptionModuleEn;
    }
    if (this.lang == "nl") {
      return this.training?.modules[moduleId].descriptionModuleNl;
    }
    if (this.lang == "fr") {
      return this.training?.modules[moduleId].descriptionModuleFr;
    }
    if (this.lang == "de") {
      return this.training?.modules[moduleId].descriptionModuleDe;
    }
    else {
      return "Unknown language"
    }
  }

  getTrainingName() {
    if (this.lang == "en") {
      return this.training?.nameEn;
    }
    if (this.lang == "nl") {
      return this.training?.nameNl;
    }
    if (this.lang == "fr") {
      return this.training?.nameFr
    }
    if (this.lang == "de") {
      return this.training?.nameDe;
    }
    else {
      return "Unknown language"
    }
  }

  getTrainingDescription() {
    if (this.lang == "en") {
      return this.training?.descriptionEn;
    }
    if (this.lang == "nl") {
      return this.training?.descriptionNl;
    }
    if (this.lang == "fr") {
      return this.training?.descriptionFr;
    }
    if (this.lang == "de") {
      return this.training?.descriptionDe;
    }
    else {
      return "Unknown language"
    }
  }

  getModuleProgressPercent(moduleId: number): number {
    if (!this.trainingProgress || !this.trainingProgress.moduleProgress) {
      return 0;
    }
    const moduleProg = this.trainingProgress.moduleProgress.find(
      (mp) => mp.moduleId === moduleId
    );
    return moduleProg ? moduleProg.percentComplete : 0;
  }


  loadProgressData(): void {
    this.ProgressService.getTrainingProgress(this.userId, this.trainingId).subscribe({
      next: (progressData) => {
        this.trainingProgress = progressData;
      },
      error: (error) => {
        console.error('Error fetching training progress', error);
      }
    });
  }

  getOverallProgressPercent(): number {
    return this.trainingProgress ? this.trainingProgress.overallPercentComplete : 0;
  }
}
