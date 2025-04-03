import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';
import { catchError, throwError } from 'rxjs';

@Component({
  selector: 'app-exam',
  templateUrl: './exam.component.html',
  styleUrls: ['./exam.component.css']
})
export class ExamComponent implements OnInit {
  exam: Exam | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService
  ) {}
    
  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam(examId);
  }
  loadExam(id: number): void {
    this.loading = true;
    this.error = null;
    
    this.examService.getExamById(id).subscribe({
      next: (exam) => {
        this.exam = exam;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message || 'Failed to load exam';
        this.loading = false;
        console.error('Error loading exam', error);
      }
    });
  }
}
