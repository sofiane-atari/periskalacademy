import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { Exam } from '../../models/exam.model';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private baseUrl = 'http://localhost:8080/api/exams'; // Adjust the backend URL as needed

  constructor(private client: HttpClient) {}

  getExams(): Observable<Exam[]> {
    return this.client.get<Exam[]>(this.baseUrl);
  }

  getExamById(id: number): Observable<Exam> {
    return this.client.get<Exam>(`${this.baseUrl}/${id}`).pipe(
      tap(exam => console.log('Received exam:', exam)), // Debug log
      catchError(error => {
        console.error('Error loading exam:', error);
        return throwError(() => error);
      })
    );
  }
  
  postExam(examData: any): Observable<any> {
    return this.client.post(`${this.baseUrl}/training/${examData.trainingId}/create`, examData);
  }
    

  updateExam(id: number, examData: any): Observable<any> {
    return this.client.put(`${this.baseUrl}/${id}`, examData);
  }

  deleteExam(id: number): Observable<any> {
    return this.client.delete(`${this.baseUrl}/delete/${id}`);
  }
  getExamByTrainingId(trainingId: number): Observable<Exam | null> {
    return this.client.get<Exam>(`${this.baseUrl}/training/${trainingId}`).pipe(
      catchError(error => {
        if (error.status === 404) {
          return of(null);
        }
        return throwError(() => error);
      })
    );
  }
}

