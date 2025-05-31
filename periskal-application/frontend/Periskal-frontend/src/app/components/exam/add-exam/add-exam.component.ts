import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { TrainingService } from '../../../services/Training/training.service';
import { Router } from '@angular/router';
import { Training } from '../../../models/training.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule, MatListOption } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatOptionModule } from '@angular/material/core';

@Component({
  selector: 'app-add-exam',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    PhotoUploadComponent,
    TranslatePipe,
    MatInputModule,
    MatTabsModule,
    MatCardModule,
    MatExpansionModule,
    MatIconModule,
    MatDividerModule,
    MatListModule,
    MatOptionModule,
  ],
  templateUrl: './add-exam.component.html',
  styleUrl: './add-exam.component.css',
})
export class AddExamComponent implements OnInit {
  examForm!: FormGroup;
  trainingsWithExams: number[] = [];
  trainingsData: any[] = [];
  isLoading = true;
  public lang: string | null = '';

  constructor(
    private fb: FormBuilder,
    private examService: ExamService,
    private router: Router,
    private trainingService: TrainingService,
    public translate: TranslateService
  ) {

    this.initForm();
    this.lang = localStorage.getItem('lang') || 'en';
  }

  initForm(): void {
    this.examForm = this.fb.group({
      trainingId: ['', Validators.required],
      examTitleEn: ['', [Validators.required, Validators.minLength(5)]],
      examTitleNl: ['', [Validators.required, Validators.minLength(5)]],
      examTitleFr: ['', [Validators.required, Validators.minLength(5)]],
      examTitleDe: ['', [Validators.required, Validators.minLength(5)]],
      examDescriptionEn: ['', [Validators.required, Validators.minLength(10)]],
      examDescriptionNl: ['', [Validators.required, Validators.minLength(10)]],
      examDescriptionFr: ['', [Validators.required, Validators.minLength(10)]],
      examDescriptionDe: ['', [Validators.required, Validators.minLength(10)]],
      totalQuestions: ['', Validators.required],
      price: ['', Validators.required],
      examAttempts: ['', [Validators.required, Validators.min(1)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      certificateValidity: [''],
      passingScore: [''],
      questions: this.fb.array([]), 
    });
  }

  get questions(): FormArray {
    return this.examForm.get('questions') as FormArray;
  }

  ngOnInit(): void {
    this.loadTrainings();
    this.addQuestion();
    this.lang = localStorage.getItem('lang');
    console.log(this.questions.controls);
  }

  addQuestion(): void {
    const answerGroup = () =>
      this.fb.group({
        textEn: ['', [Validators.required, Validators.minLength(1)]],
        textNl: ['', [Validators.required, Validators.minLength(1)]],
        textFr: ['', [Validators.required, Validators.minLength(1)]],
        textDe: ['', [Validators.required, Validators.minLength(1)]],
      });

    const questionGroup = this.fb.group({
      textEn: ['', [Validators.required, Validators.minLength(5)]],
      textNl: ['', [Validators.required, Validators.minLength(5)]],
      textFr: ['', [Validators.required, Validators.minLength(5)]],
      textDe: ['', [Validators.required, Validators.minLength(5)]],
      photos: [[]],
      answers: this.fb.array([
        answerGroup(),
        answerGroup(),
        answerGroup(),
        answerGroup(),
      ]),
      correctAnswer: ['', Validators.required],
    });
    this.questions.push(questionGroup);
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  getAnswers(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('answers') as FormArray;
  }

  loadTrainings(): void {
    this.isLoading = true;
    this.trainingService.getTrainingData().subscribe({
      next: (data: any[]) => {
        this.trainingsData = data;
        this.checkExistingExams();
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
      },
    });
  }

  checkExistingExams(): void {
    const checks = this.trainingsData.map((training) => {
      return this.examService
        .getExamByTrainingId(training.id)
        .toPromise()
        .then((exam) => {
          if (exam) {
            this.trainingsWithExams.push(training.id);
          }
        });
    });

    Promise.all(checks).finally(() => {
      this.isLoading = false;
    });
  }

  hasExam(trainingId: number): boolean {
    return this.trainingsWithExams.includes(trainingId);
  }

  submitExam(): void {
    console.log(this.examForm);
    if (this.examForm.valid) {
      const examData = {
        examTitleEn: this.examForm.value.examTitleEn,
        examTitleNl: this.examForm.value.examTitleNl,
        examTitleFr: this.examForm.value.examTitleFr,
        examTitleDe: this.examForm.value.examTitleDe,
        examDescriptionEn: this.examForm.value.examDescriptionEn,
        examDescriptionNl: this.examForm.value.examDescriptionNl,
        examDescriptionFr: this.examForm.value.examDescriptionFr,
        examDescriptionDe: this.examForm.value.examDescriptionDe,
        totalQuestions: Number(this.examForm.value.totalQuestions),
        trainingId: Number(this.examForm.value.trainingId),
        questions: this.questions.controls.map((q: any) => ({
          textEn: q.value.textEn,
          textNl: q.value.textNl,
          textFr: q.value.textFr,
          textDe: q.value.textDe,
          photos: (q.value.photos || []).map((photo: any) => ({
            imageData: photo.imageData,
          })),
          answers: q.value.answers.map((a: any) => ({
            textEn: a.textEn,
            textNl: a.textNl,
            textFr: a.textFr,
            textDe: a.textDe,
          })),
          correctAnswer: Number(q.value.correctAnswer),
        })),
        price: Number(this.examForm.value.price),
        examAttempts: Number(this.examForm.value.examAttempts),
        duration: Number(this.examForm.value.duration),
        certificateValidity: Number(this.examForm.value.certificateValidity),
        passingScore: Number(this.examForm.value.passingScore),

      };
      console.log(examData);
      this.examService.createExam(examData).subscribe({
        next: (response) => {
          console.log(response);
          this.router.navigate(['/training-employee']);
        },
        error: (error) => {},
      });
    }
  }

  getTrainingName(training: Training) {
    if (this.lang == 'en') {
      return training?.nameEn;
    }
    if (this.lang == 'nl') {
      return training?.nameNl;
    }
    if (this.lang == 'fr') {
      return training?.nameFr;
    }
    if (this.lang == 'de') {
      return training?.nameDe;
    } else {
      return 'Unknown language';
    }
  }

  private getLangKey(): string {
    switch (this.lang) {
      case 'en': return 'En';
      case 'nl': return 'Nl';
      case 'fr': return 'Fr';
      case 'de': return 'De';
      default: return 'En';
    }
  }
  getQuestionDisplayText(questionIndex: number): string {
    const langKey = this.getLangKey();
    return this.questions.at(questionIndex)?.get(`text${langKey}`)?.value 
      || this.translate.instant('NO_QUESTION_ENTERED');
  }
}
