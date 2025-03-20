import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { JwtDecoderService } from '../services/JwtDecode/jwt-decoder.service';

@Component({
  selector: 'app-training-employee',
  imports: [TranslatePipe],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})

export class TrainingEmployeeComponent implements OnInit {
  public trainingsData: training[] = [];

  constructor(private router: Router, private svc: TrainingService, private jwtDecoderService: JwtDecoderService ) { }


  ngOnInit(): void {
    const role = this.jwtDecoderService.getUserRole();
    const products: string[] = this.jwtDecoderService.getProducts() || [];
  
    this.svc.getTrainingData().subscribe(
      (data: training[]) => {       
        if (role === "Skipper" || role === "Fleetmanager") {
          if (products && products.length > 0) {
            this.trainingsData = data.filter(training => {
              return products.some(product => 
                training.name.toLowerCase().includes(product.toLowerCase())
              );
            });
          } else {
            this.trainingsData = [];
          }
        }
        else if (role === "ADMIN" || role === "Support") {
          this.trainingsData = data;
        } 
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
