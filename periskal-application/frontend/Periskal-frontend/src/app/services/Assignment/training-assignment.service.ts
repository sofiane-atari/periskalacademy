import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TrainingAssignment } from '../../models/training-assignment.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TrainingAssignmentService {

  private baseUrl = `${environment.apiUrl}/assignments`;

  constructor(private http: HttpClient) { }

  assignTrainingToSkippers(trainingId: number, skipperIds: string[], assignedBy: string): Observable<any> {
    const assignments = skipperIds.map(skipperId => ({
      skipperId,
      trainingId,
      assignedDate: new Date(),
      assignedBy
    }));
    return this.http.post(`${this.baseUrl}`, assignments);
  }
  
  getSkipperAssignments(skipperId: string): Observable<TrainingAssignment[]> {
    return this.http.get<TrainingAssignment[]>(`${this.baseUrl}/${skipperId}`);
  }
  
  checkAssignment(skipperId: string, trainingId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/${skipperId}/${trainingId}`);
  }

}
