import { Component, OnInit, QueryList } from '@angular/core';
import { Exam } from '../../../models/exam.model';
import { ExamService } from '../../../services/Exam/exam.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-update-exam',
  imports: [FormsModule, TranslatePipe],
  templateUrl: './update-exam.component.html',
  styleUrl: './update-exam.component.css'
})
export class UpdateExamComponent implements OnInit {
  examId!: number;
  exam: Exam = {
    id: 0, examTitle: '', examDescription: '',
    trainingId: 0,
    totalQuestions: 0,
    questions: [],
    price: 0,
    examAttempts: 0,
  };

  constructor(
    private examService: ExamService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam();
  }

  loadExam(): void {
    this.examService.getExamById(this.examId).subscribe({
      next: (data) => (this.exam = data),
      error: (err) => console.error('Examen ophalen mislukt', err)
    });
  }

  deleteExam(trainingId: number): void {
    if (!confirm('Weet u zeker dat u dit examen wilt verwijderen?')) return;

    this.examService.deleteExam(trainingId).subscribe({
      next: () => {
        this.router.navigate(['/training-employee'], {
          onSameUrlNavigation: 'reload' // Force page reload
        });
      },
      error: (error) => {
        console.error('Delete failed:', error);
        alert('Verwijderen mislukt: ' + error.message);
      }
    });
    this.loadExam();
  }
  confirmDeleteExam(): void {
    if (!this.exam || this.exam.id == null) return;
    if (!confirm('Weet u zeker dat u dit examen wilt verwijderen?')) return;
    this.deleteExam(this.exam?.id);
  }

  onSubmit(): void {
    this.examService.updateExam(this.examId, this.exam).subscribe({
      next: () => {
        alert('Examen succesvol aangepast!');
        this.router.navigate(['/trainingEmployee']);
      },
      error: (err) => console.error('Examen updaten mislukt', err)
    });
  }

  addQuestion(): void {
    this.exam.questions.push({
      id: 0, text: '',
      correctAnswer: 0
    });
  }

  removeQuestion(index: number): void {
    this.exam.questions.splice(index, 1);
  }
}