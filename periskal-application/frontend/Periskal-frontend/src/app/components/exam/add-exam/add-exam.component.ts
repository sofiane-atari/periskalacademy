import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { TrainingService } from '../../../services/Training/training.service';
import { Router } from '@angular/router';
import { Training } from '../../../models/training.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { TranslatePipe } from '@ngx-translate/core';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-add-exam',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, PhotoUploadComponent, TranslatePipe, MatTabsModule],
  templateUrl: './add-exam.component.html',
  styleUrl: './add-exam.component.css'
})
export class AddExamComponent implements OnInit {
  examForm: FormGroup;
  trainingsWithExams: number[] = [];
  trainingsData: any[] = [];
  isLoading = true;
  public lang: string | null = "";

  constructor(
    private fb: FormBuilder,
    private examService: ExamService,
    private router: Router,
    private trainingService: TrainingService
  ) {
    this.examForm = this.fb.group({
      trainingId: ['', Validators.required],
      examTitle: ['', Validators.required],
      examDescription: ['', Validators.required],
      totalQuestions: ['', Validators.required],
      price: ['', Validators.required],
      examAttempts: ['', Validators.required],
      duration: [''],
      certificateValidity: [''],
      passingScore: [''],
      questions: this.fb.array([])// Initialize empty FormArray
    });
    this.lang = localStorage.getItem('lang') || 'en';
  }

  initForm(): void {
  }

  get questions(): FormArray {
    return this.examForm.get('questions') as FormArray;
  }

  ngOnInit(): void {
    this.loadTrainings();
    this.lang = localStorage.getItem('lang');
  }

  addQuestion(): void {

    const answerGroup = () => this.fb.group({
      text: ['', Validators.required]
    });

    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      photos: [[]],
      answers: this.fb.array([
        answerGroup(),
        answerGroup(),
        answerGroup(),
        answerGroup()
      ]),
      correctAnswer: ['', Validators.required]
    });
    this.questions.push(questionGroup);
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  getQuestions(): FormArray {
    return this.examForm.get('questions') as FormArray;
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
      }
    });
  }

  checkExistingExams(): void {
    const checks = this.trainingsData.map(training => {
      return this.examService.getExamByTrainingId(training.id).toPromise()
        .then(exam => {
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
        examTitle: this.examForm.value.examTitle,
        examDescription: this.examForm.value.examDescription,
        totalQuestions: Number(this.examForm.value.totalQuestions),
        trainingId: Number(this.examForm.value.trainingId),
        questions: this.questions.controls.map(q => ({
          text: q.value.text,
          photos: (q.value.photos || []).map((photo: any) => ({
            imageData: photo.imageData
          })),
          answers: q.value.answers.map((a: any) => ({
            text: a.text
          })),
          correctAnswer: Number(q.value.correctAnswer)
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
        error: (error) => {
        }
      });
    }
  }

  getTrainingName(training: Training) {
    if (this.lang == "en") {
      return training?.nameEn
    }
    if (this.lang == "nl") {
      return training?.nameNl
    }
    if (this.lang == "fr") {
      return training?.nameFr
    }
    if (this.lang == "de") {
      return training?.nameDe
    }
    else {
      return "Unknown language"
    }
  }
}