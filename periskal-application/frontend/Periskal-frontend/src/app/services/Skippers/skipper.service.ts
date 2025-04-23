import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface Skipper {
  id: string;
  username: string;
  firstname: string;
  lastname: string;
  shipname: string;
  role: string;
  products: string[];
}


@Injectable({
  providedIn: 'root'
})
export class SkipperService {

  private baseUrl = 'http://localhost:8080/api';
  private apiUrl = `${this.baseUrl}/skippers`;

  constructor(private http: HttpClient) { }

  getSkippers() {
    return this.http.get<Skipper[]>(this.apiUrl);
  }

}
