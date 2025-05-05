// take-exam.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';
import { Question } from '../../../models/question.model';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-take-exam',
  templateUrl: './take-exam.component.html',
  imports: [TranslatePipe],
  styleUrls: ['./take-exam.component.css']
})
export class TakeExamComponent implements OnInit {
  exam: Exam | null = null;
  currentQuestionIndex = 0;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam(examId);
  }

  loadExam(examId: number): void {
    this.loading = true;
    this.error = null;

    this.examService.getExamById(examId).subscribe({
      next: (exam) => {
        console.log('Exam with questions:', exam); // Debug
        this.exam = exam;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load exam:', err);
        this.error = 'Failed to load exam questions';
        this.loading = false;
      }
    });
  }

  get currentQuestion(): Question | null {
    if (!this.exam || !this.exam.questions.length) return null;
    return this.exam.questions[this.currentQuestionIndex];
  }

  nextQuestion(): void {
    if (this.exam && this.currentQuestionIndex < this.exam.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  submitExam(): void {
    // Implementeer je inzendlogica hier
    console.log('Exam submitted');
    this.router.navigate(['/exam-result']);
  }
}