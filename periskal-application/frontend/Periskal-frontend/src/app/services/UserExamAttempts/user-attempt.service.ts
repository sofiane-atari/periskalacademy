import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserAttemptService {

  constructor(private client: HttpClient) { }

  checkUserAttempts(examId: number, userId: String) {
    return this.client.get(`http://backend.localhost:8080/api/attempts/${examId}/${userId}`);
  }
}
