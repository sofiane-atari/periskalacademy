import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TrainingProgress } from '../../models/progress.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProgressService {
  private baseUrl = `${environment.apiUrl}/progress`;

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
