import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserAttemptService {

  constructor(private client: HttpClient) { }

  private readonly attemptsUrl = `${environment.apiUrl}/attempts`;


  startExam(examId: number, userId: String) {
    return this.client.post(`${this.attemptsUrl}/${userId}/${examId}/start`, null);
  }

  getRemainingAttempts(userId: string, examId: string): Observable<{ remainingAttempts: number }> {
    return this.client.get<{ remainingAttempts: number }>(`${this.attemptsUrl}/${userId}/${examId}/remaining`);
  }
}
