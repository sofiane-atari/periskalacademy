import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

export interface Test {
  id: number;
  test: string;
}

@Component({
  selector: 'app-test',
  imports: [],
  templateUrl: './test.component.html',
  styleUrl: './test.component.css'
})
export class TestComponent implements OnInit {

  backendMessage: string = 'Checking backend...';

  constructor(private http: HttpClient) {}
  tests: Test[] = [];

  ngOnInit(): void {
    this.http.get<Test[]>('http://backend.localhost:8080/api/test').subscribe(
      (data) => {
        this.tests = data;
      },
      (error) => {
        console.error('Error fetching tests:', error);
      }
    );
  }
}
