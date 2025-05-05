import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { TrainingService } from '../../../services/Training/training.service';
import { Router } from '@angular/router';
import { Training } from '../../../models/training.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';

@Component({
  selector: 'app-add-exam',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, PhotoUploadComponent],
  templateUrl: './add-exam.component.html',
  styleUrl: './add-exam.component.css'
})
export class AddExamComponent implements OnInit {
    examForm: FormGroup;
    trainingsWithExams: number[] = [];
    trainingsData: any[] = [];
    isLoading = true;

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
      questions: this.fb.array([]) // Initialize empty FormArray
    });
  }

  initForm(): void {
  }
  get questions(): FormArray {
    return this.examForm.get('questions') as FormArray;
  }
  ngOnInit(): void {
    this.loadTrainings();
  }

  addQuestion(): void {
    const questionGroup = this.fb.group({
      text:   ['', Validators.required],
      photos: [[]]
    });
    this.questions.push(questionGroup);
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  loadTrainings(): void {
    this.isLoading = true;
    this.trainingService.getTrainingData().subscribe({
      next: (data: any[]) => {
        console.log('Ontvangen trainingsdata:', data); // Debug log
        this.trainingsData = data;
        console.log('TrainingsData na toewijzing:', this.trainingsData); // Debug log
        this.checkExistingExams();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Fout bij ophalen trainingen', error);
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
    if (this.examForm.valid) {
      const examData = {
        examTitle: this.examForm.value.examTitle,
        examDescription: this.examForm.value.examDescription,
        trainingId: Number(this.examForm.value.trainingId),
        questions: this.questions.controls.map(q => ({
          text: q.value.text,
          photos: (q.value.photos || []).map((photo: any) => ({
            imageData: photo.imageData  // This matches your Photo entity constructor
          }))
        }))
      };
  
      console.log('Submitting exam data:', examData);
      this.examService.createExam(examData).subscribe({
        next: (response) => {
          this.router.navigate(['/training-employee']);
        },
        error: (error) => {
          console.error('Error:', error);
        }
      });
    }
  }
}