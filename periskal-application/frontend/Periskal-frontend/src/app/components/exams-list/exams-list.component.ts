import { CommonModule } from '@angular/common';
import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableDataSource } from '@angular/material/table';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { CompletedExam } from '../../models/completed-exam.model';
import { CompletedExamService } from '../../services/CompletedExam/completed-exam.service';



@Component({
  selector: 'app-profile', 
  standalone: true,
  imports: [CommonModule,MatTableModule,MatSortModule,MatProgressSpinnerModule],
  templateUrl: './exams-list.component.html',
  styleUrls: ['./exams-list.component.css']
})

export class ExamsListComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = ['examName', 'trainingName', 'score', 'requiredScore', 'completedDate'];
  dataSource = new MatTableDataSource<CompletedExam>([]);
  isLoading = true;
  error: string | null = null;
  currentUserId: string | null = null; 
  

  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private completedExamService: CompletedExamService,
    private jwtDecoderService: JwtDecoderService,
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.jwtDecoderService.getId(); 
    this.loadCompletedExams();
  }

  loadCompletedExams(): void {
    this.isLoading = true;
    this.error = null;
    
    if (!this.currentUserId) { 
         this.error = "User ID is missing. Cannot load exams.";
         this.isLoading = false;
         this.dataSource.data = []; 
         return;
    }

    this.completedExamService.getUserCompletedExams(this.currentUserId).subscribe({
      next: (data: CompletedExam[]) => {
        this.dataSource.data = data;
        this.isLoading = false;
      },
      error: (err: { message: any; }) => {
        console.error('Error fetching completed exams:', err);
        this.error = `Failed to load exams: ${err.message || 'Server error'}`;
        this.isLoading = false;
        this.dataSource.data = []; 
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }
  
}