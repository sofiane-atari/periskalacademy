import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { Exam } from '../../models/exam.model';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private baseUrl = 'http://localhost:8080/api/exams';

  constructor(private client: HttpClient) { }

  getExams(): Observable<Exam[]> {
    return this.client.get<Exam[]>(this.baseUrl);
  }

  getExamById(id: number): Observable<Exam> {
    return this.client.get<Exam>(`${this.baseUrl}/${id}`).pipe(
      tap(exam => {
        if (!exam.questions) {
          exam.questions = [];
        }
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
    return this.client.delete(`${this.baseUrl}/${id}`);
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
  createExam(examData: any): Observable<Exam> {
    const payload = {
      examTitle: examData.examTitle,
      examDescription: examData.examDescription,
      totalQuestions: examData.totalQuestions,
      trainingId: examData.trainingId,
      questions: examData.questions.map((q: any) => ({
        text: q.text,
        photos: (q.photos || []).map((photo: any) => ({
          imageData: photo.imageData
        })),
        answers: q.answers.map((a: any) => ({ text: a.text })),
        correctAnswer: q.correctAnswer
      })),
      price: examData.price,
      examAttempts: examData.examAttempts,
      duration: examData.duration
    };
    return this.client.post<Exam>(this.baseUrl, payload);
  }
}