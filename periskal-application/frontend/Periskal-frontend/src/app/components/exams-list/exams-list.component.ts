import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { CompletedExam } from '../../models/completed-exam.model';
import { CompletedExamService } from '../../services/CompletedExam/completed-exam.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-exams-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatSortModule, MatProgressSpinnerModule],
  templateUrl: './exams-list.component.html',
  styleUrl: './exams-list.component.css'
})
export class ExamsListComponent implements OnInit {
  displayedColumns: string[] = ['examName', 'trainingName', 'score', 'requiredScore'];
  dataSource = new MatTableDataSource<CompletedExam>([]);
  isLoading = true;
  error: string | null = null;

  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private completedExamService: CompletedExamService
  ) {}

  ngOnInit(): void {
    this.loadCompletedExams();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  loadCompletedExams(): void {
    this.isLoading = true;
    this.completedExamService.getUserCompletedExams().subscribe({
      next: (exams) => {
        this.dataSource.data = exams;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading completed exams', err);
        this.error = 'Failed to load exams. Please try again later.';
        this.isLoading = false;
      }
    });
  }
}
