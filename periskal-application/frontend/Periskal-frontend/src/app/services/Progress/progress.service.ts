import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { TrainingProgress } from '../../models/progress.model';

@Injectable({
  providedIn: 'root'
})
export class ProgressService {
  private baseUrl = 'http://localhost:8080/api/progress'

  constructor(
    private http: HttpClient
  ) { }

  getMockUserProgress(userId: string) : Observable<TrainingProgress[]> { // TODO : remove mock data and implement real API call

    const mockProgress: TrainingProgress = {
      id: 0,
      userId: userId,
      trainingId: 1,
      moduleProgress: [
        {
          moduleId: 0,
          percentComplete: 75,
          completedChapters: [1, 2, 3]
        },
        {
          moduleId: 1,
          percentComplete: 25,
          completedChapters: [4]
        }
      ],
      overallPercentComplete: 50,
      lastUpdated: new Date(),
    };
    
    return of([mockProgress]);
  }

  // To be implemented for real API calls later 
  /*
  completeChapter(userId: string, trainingId: number, moduleId: number, chapterId: number): Observable<TrainingProgress> {
    return 
  } */
  
  getUserProgress(userId: string): Observable<TrainingProgress[]> {
    return this.getMockUserProgress(userId); 
  }
  
  getTrainingProgress(userId: string, trainingId: number): Observable<TrainingProgress | undefined> {
    return this.getMockUserProgress(userId).pipe(
      map(progressList => progressList.find(p => p.trainingId === trainingId))
    );
  }
}
