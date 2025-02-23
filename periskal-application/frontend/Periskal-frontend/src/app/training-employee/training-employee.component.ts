import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';

@Component({
  selector: 'app-training-employee',
  imports: [],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})
export class TrainingEmployeeComponent implements OnInit {
  public trainingsData : training[] = [];

  constructor(private router: Router, private svc: TrainingService) { }


  ngOnInit(): void {
    this.svc.getTrainingData().subscribe(
      (data: training[]) => {
        this.trainingsData = data;  // Assign the received data to the array
      },
      (error) => {
        console.error('Error fetching trainings', error);
      }
    );
  }
  goToTrainingDetails(trainingId: number) {
    this.router.navigate(['/training', trainingId]);
  }

  goToAddPage() {
    this.router.navigate(['/submit'])
  }
}

export interface training {
  id: number
  name: String
  description: String
  products: product[]
}
export interface product {
  id: number
  name: String
  description: String
}
