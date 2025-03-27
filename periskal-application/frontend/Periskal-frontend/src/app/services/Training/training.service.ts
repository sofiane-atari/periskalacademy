import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { training } from '../../components/Training/training-employee/training-employee.component';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  constructor(private client: HttpClient) { }

  getTrainingData(): Observable<training[]> {
    return this.client.get<training[]>("http://backend.localhost:8080/api/trainings");
  }

  getTrainingById(trainingId: number): Observable<training> {
    return this.client.get<training>(`http://backend.localhost:8080/api/trainings/${trainingId}`);
  }

  postTraining(trainingData: any): Observable<any> {
    return this.client.post("http://localhost:8080/api/trainings", trainingData);
  }

  updateTraining(trainingId: number, trainingData: any): Observable<any> {
    return this.client.put(`http://localhost:8080/api/trainings/${trainingId}`, trainingData);
  }

  deleteTraining(trainingId: number): Observable<any> {
    return this.client.delete(`http://localhost:8080/api/trainings/${trainingId}`)
  }

  deleteModule(trainingId: number, moduleId: number): Observable<any> {
    return this.client.delete(`http://localhost:8080/api/trainings/${trainingId}/modules/${moduleId}`)
  }
  updateModules(trainingId: number, modules: any): Observable<any> {
    return this.client.put(`http://localhost:8080/api/trainings/${trainingId}/modules`, modules)
  }
  addModuleToTraining(trainingId: number, module: any): Observable<any> {
    return this.client.post(`http://localhost:8080/api/trainings/${trainingId}/modules`, module);
  }
}
