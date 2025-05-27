import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TrainingProgress } from '../../models/progress.model';

@Injectable({
  providedIn: 'root'
})
export class ProgressService {
  private baseUrl = 'http://localhost:8080/api/progress'

  constructor(
    private http: HttpClient
  ) { }


  completeChapter(userId: string, trainingId: number, moduleId: number, chapterId: number): Observable<TrainingProgress> {
    const request = {
      userId,
      trainingId,
      moduleId,
      chapterId
    };
    return this.http.post<TrainingProgress>(`${this.baseUrl}/complete-chapter`, request); 
  } 
  
  getUserProgress(userId: string): Observable<TrainingProgress[]> {

    return this.http.get<TrainingProgress[]>(`${this.baseUrl}/user/${userId}`);
  }
  
  getTrainingProgress(userId: string, trainingId: number): Observable<TrainingProgress | undefined> {

    return this.http.get<TrainingProgress>(`${this.baseUrl}/training/${trainingId}/user/${userId}`);
  }
}
