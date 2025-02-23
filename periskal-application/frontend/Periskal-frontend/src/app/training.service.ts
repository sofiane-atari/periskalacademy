import { Injectable } from '@angular/core';
import { product, training } from './training-employee/training-employee.component';
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
}
