import { Injectable } from '@angular/core';
import { module, training } from './training-employee/training-employee.component';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  constructor(private client : HttpClient) { }

  getTrainingData() : Observable<training[]> {
    return this.client.get<training[]>("http://backend.localhost:8080/api/trainings");
  }

  getTrainingById(trainingId: number): Observable<training> {
    return this.client.get<training>(`http://backend.localhost:8080/api/trainings/${trainingId}`);
  }

  postTraining(trainingData: any): Observable<any> {
    return this.client.post("http://localhost:8080/api/trainings/create", trainingData);
  }
}
