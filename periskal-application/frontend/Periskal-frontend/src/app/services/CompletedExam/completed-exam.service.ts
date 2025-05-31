import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs';
import { CompletedExam } from '../../models/completed-exam.model'; 
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompletedExamService {

  
  private readonly baseUrl = `${environment.apiUrl}/completed-exams`;

  constructor(
    private readonly http: HttpClient
  ) {}

  getUserCompletedExams(userId: string): Observable<CompletedExam[]> {
    
    return this.http.get<CompletedExam[]>(`${this.baseUrl}/user/${userId}`);
  }

  
  saveCompletedExam(examData: CompletedExam): Observable<CompletedExam> {
    return this.http.post<CompletedExam>(this.baseUrl, examData);
  }

  
}