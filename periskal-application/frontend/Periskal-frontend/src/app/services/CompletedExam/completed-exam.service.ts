import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CompletedExam } from '../../models/completed-exam.model'; 

@Injectable({
  providedIn: 'root'
})
export class CompletedExamService {
  private baseUrl = 'http://localhost:8080/api/completed-exams';

  constructor(
    private http: HttpClient
  ) {}

  getUserCompletedExams(userId: string): Observable<CompletedExam[]> {
    
    return this.http.get<CompletedExam[]>(`${this.baseUrl}/user/${userId}`);
  }

  
  saveCompletedExam(examData: CompletedExam): Observable<CompletedExam> {
    return this.http.post<CompletedExam>(this.baseUrl, examData);
  }

  
}