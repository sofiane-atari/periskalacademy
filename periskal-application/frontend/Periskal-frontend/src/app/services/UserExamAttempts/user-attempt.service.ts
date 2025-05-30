import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserAttemptService {

  constructor(private client: HttpClient) { }

  startExam(examId: number, userId: String) {
    return this.client.post(`http://backend.localhost:8080/api/attempts/${userId}/${examId}/start`, null);
  }

  getRemainingAttempts(userId: string, examId: string): Observable<{ remainingAttempts: number }> {
    return this.client.get<{ remainingAttempts: number }>(`http://backend.localhost:8080/api/attempts/${userId}/${examId}/remaining`);
  }
}
