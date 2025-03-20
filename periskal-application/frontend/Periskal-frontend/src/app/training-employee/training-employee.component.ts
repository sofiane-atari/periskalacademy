import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-training-employee',
  imports: [TranslatePipe],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})
export class TrainingEmployeeComponent implements OnInit {
  public trainingsData: training[] = [];

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
    this.router.navigate(['/add'])
  }
  goToEditPage(trainingId: number) {
    this.router.navigate(['/edit', trainingId])
  }
}

export interface training {
  id: number
  name: String
  description: String
  modules: module[]
}
export interface module {
  id: number
  nameModule: String
  descriptionModule: String
  chapters: chapter[]
}

export interface chapter {
  id: number
  nameChapter: String
  descriptionChapter: String
  textChapter: String
}
