import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { Exam } from '../../models/exam.model';
import { Photo } from '../../models/photo.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private readonly baseUrl = `${environment.apiUrl}/exams`;
  private readonly photoUrl = `${environment.apiUrl}/photos`;


  constructor(private client: HttpClient) { }


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
      examTitleEn: examData.examTitleEn,
      examTitleNl: examData.examTitleNl,
      examTitleFr: examData.examTitleFr,
      examTitleDe: examData.examTitleDe,
      examDescriptionEn: examData.examDescriptionEn,
      examDescriptionNl: examData.examDescriptionNl,
      examDescriptionFr: examData.examDescriptionFr,
      examDescriptionDe: examData.examDescriptionDe,
      totalQuestions: examData.totalQuestions,
      trainingId: examData.trainingId,
      questions: examData.questions.map((q: any) => ({
      textEn: q.textEn,
      textNl: q.textNl,
      textFr: q.textFr,
      textDe: q.textDe,
      photos: (q.photos || []).map((photo: any) => ({
        imageData: photo.imageData
      })),
      answers: q.answers.map((a: any) => ({
        textEn: a.textEn,
        textNl: a.textNl,
        textFr: a.textFr,
        textDe: a.textDe,
      })),
      correctAnswer: q.correctAnswer
    })),
      price: examData.price,
      examAttempts: examData.examAttempts,
      duration: examData.duration,
      certificateValidity: examData.certificateValidity,
      passingScore: examData.passingScore
    };
    return this.client.post<Exam>(this.baseUrl, payload);
  }

  deletePhoto(photoId: number) {
    return this.client.delete(`${this.photoUrl}/${photoId}`);
  }

  addPhotos(examId: number, photos?: Photo[]) {
    return this.client.post(`${this.photoUrl}/${examId}`, photos);
  }

}