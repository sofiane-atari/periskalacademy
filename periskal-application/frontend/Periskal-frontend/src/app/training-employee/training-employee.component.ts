import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';

@Component({
  selector: 'app-training-employee',
  imports: [],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})
export class TrainingEmployeeComponent {
  private trainingSvc : TrainingService;
  public trainingsData : training[];

  constructor(private router: Router, private svc: TrainingService) {
    this.trainingSvc = svc;
    this.trainingsData = this.trainingSvc.getTrainingData();
  }

  goToTrainingDetails(trainingId: number) {
    this.router.navigate(['/training', trainingId]);
  }

}

export interface training {
  id: number
  name: String
  description: String
  Products: product[]
}
export interface product {
  id: number
  name: String
  description: String
}
