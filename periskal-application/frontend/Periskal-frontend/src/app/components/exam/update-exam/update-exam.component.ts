import { Component, OnInit, QueryList } from '@angular/core';
import { Exam } from '../../../models/exam.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { MatTab, MatTabsModule } from '@angular/material/tabs';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';
import { Photo } from '../../../models/photo.model';

@Component({
  selector: 'app-update-exam',
  imports: [FormsModule, TranslatePipe, MatTabsModule, PhotoUploadComponent],
  templateUrl: './update-exam.component.html',
  styleUrl: './update-exam.component.css',
})
export class UpdateExamComponent implements OnInit {
  examId!: number;
  newQuestionPhotos: {[key: string]: Photo[]} = {};
  isLoading = true;
  public lang: string = localStorage.getItem('lang') || 'en';
  exam: Exam = {
    id: 0,
    examTitleEn: '',
    examTitleNl: '',
    examTitleFr: '',
    examTitleDe: '',
    examDescriptionEn: '',
    examDescriptionNl: '',
    examDescriptionFr: '',
    examDescriptionDe: '',
    trainingId: 0,
    totalQuestions: 0,
    questions: [],
    price: 0,
    examAttempts: 0,
    duration: 0,
  };

  constructor(
    private examService: ExamService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam();
  }

  loadExam(): void {
    this.examService.getExamById(this.examId).subscribe({
      next: (data) => {
        this.exam = data;
        this.exam.questions.forEach(question => {
          if (!question.answers || question.answers.length === 0) {
            question.answers = [
              { textEn: '', textNl: '', textFr: '', textDe: '' },
              { textEn: '', textNl: '', textFr: '', textDe: '' },
              { textEn: '', textNl: '', textFr: '', textDe: '' },
              { textEn: '', textNl: '', textFr: '', textDe: '' }
            ];
          }
          question.photos = question.photos || [];
        });
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.showErrorSnackbar('LOAD_EXAM_ERROR', error);
      }
    });
  }

  deleteExam(trainingId: number): void {
    if (!confirm('Weet u zeker dat u dit examen wilt verwijderen?')) return;

    this.examService.deleteExam(this.examId).subscribe({
      next: () => {
        this.showSuccessSnackbar('EXAM_DELETED_SUCCESS');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.showErrorSnackbar('DELETE_FAILED', error);
      }
    });

  }
  confirmDeleteExam(): void {
    if (!this.exam || this.exam.id == null) return;
    if (!confirm('Weet u zeker dat u dit examen wilt verwijderen?')) return;
    this.deleteExam(this.exam?.id);
  }

  onSubmit(): void {
    this.examService.updateExam(this.examId, this.exam).subscribe({
      next: () => {
        this.showSuccessSnackbar('EXAM_UPDATED_SUCCESS');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.showErrorSnackbar('UPDATE_FAILED', error);
      }
    });
  }

  addQuestion(): void {
    this.exam.questions.push({
      id: 0,
      textEn: '',
      textNl: '',
      textFr: '',
      textDe: '',
      correctAnswer: 0,
      photos: [],
      answers: [
        { textEn: '', textNl: '', textFr: '', textDe: '' },
        { textEn: '', textNl: '', textFr: '', textDe: '' },
        { textEn: '', textNl: '', textFr: '', textDe: '' },
        { textEn: '', textNl: '', textFr: '', textDe: '' }
      ]
    });
  }

  removeQuestion(index: number): void {
    this.exam.questions.splice(index, 1);
  }

  removeQuestionPhoto(questionIndex: number, photoIndex: number): void {
    if (this.exam?.questions?.[questionIndex]?.photos) {
      this.exam.questions[questionIndex].photos.splice(photoIndex, 1);
    }
  }

   private showSuccessSnackbar(translationKey: string): void {
    const message = this.translate.instant(translationKey);
    const config: MatSnackBarConfig = {
      duration: 3000,
      verticalPosition: 'bottom',
      horizontalPosition: 'left',
      panelClass: ['snackbar-success'],
      politeness: 'assertive'
    };
    this.snackBar.open(message, 'Close', config);
  }

  private showErrorSnackbar(translationKey: string, error?: any): void {
    let message = this.translate.instant(translationKey);
    if (error?.message) {
      message += `: ${error.message}`;
    }
    
    const config: MatSnackBarConfig = {
      duration: 5000,
      verticalPosition: 'bottom',
      horizontalPosition: 'left',
      panelClass: ['snackbar-error'],
      politeness: 'assertive'
    };
    this.snackBar.open(message, 'Close', config);
  }

  getQuestionText(question: any): string {
  if (!question) return this.translate.instant('NO_QUESTION_ENTERED');
  
  switch(this.lang) {
    case 'en': return question.textEn || this.translate.instant('NO_QUESTION_ENTERED');
    case 'nl': return question.textNl || this.translate.instant('NO_QUESTION_ENTERED');
    case 'fr': return question.textFr || this.translate.instant('NO_QUESTION_ENTERED');
    case 'de': return question.textDe || this.translate.instant('NO_QUESTION_ENTERED');
    default: return question.textEn || this.translate.instant('NO_QUESTION_ENTERED');
  }
}
}
