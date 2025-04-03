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
    public trainingsData: Training[] = [];
    isLoading = true;

  constructor(
    private fb: FormBuilder,
    private examService: ExamService,
    private router: Router,
    private trainigService: TrainingService
  ) {
    this.examForm = this.fb.group({
      trainingId: ['', Validators.required], // Dropdown selection
      examTitle: ['', Validators.required],
      examDescription: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadTrainings();
  }
  loadTrainings(): void {
    this.trainigService.getTrainingData().subscribe({
      next: (data: Training[]) => {
        this.trainingsData = data;
        this.checkExistingExams();
      },
      error: (error) => {
        console.error('Error fetching trainings', error);
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
        examDescription: this.examForm.value.examDescription
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