import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { TrainingService } from '../../../services/Training/training.service';
import { Router } from '@angular/router';
import { Training } from '../../../models/training.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';

@Component({
  selector: 'app-add-exam',
  imports: [FormsModule, ReactiveFormsModule],
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
    this.examForm = this.fb.group({
      trainingId: ['', Validators.required],
      examTitle: ['', Validators.required],
      examDescription: ['', Validators.required],
      questions: this.fb.array([]) // FormArray voor vragen
    });
  }
  get questions(): FormArray {
    return this.examForm.get('questions') as FormArray;
  }
  ngOnInit(): void {
    this.loadTrainings();
  }
  addQuestion(): void {
    const questionGroup = this.fb.group({
      text: ['', Validators.required]
    });
    this.questions.push(questionGroup);
  }
  
  // Remove a question from the form
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
      const trainingId = Number(this.examForm.value.trainingId);
  
      const examData: Exam = {
        trainingId: trainingId,
        examTitle: this.examForm.value.examTitle,
        examDescription: this.examForm.value.examDescription,
        questions: []
      };
  
      this.examService.postExam(examData).subscribe(
        (response) => {
          console.log('Exam added successfully!', response);
          this.router.navigate(['/trainingEmployee']); 
        },
        (error) => {
          console.error('Error adding exam', error);
        }
      );
    }
  }
}