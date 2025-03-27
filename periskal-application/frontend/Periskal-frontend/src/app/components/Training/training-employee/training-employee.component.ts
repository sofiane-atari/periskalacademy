import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { RoleAuthorizationService } from '../../../services/RoleAuthorization/role-authorization.service';


@Component({
  selector: 'app-training-employee',
  imports: [TranslatePipe],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})

export class TrainingEmployeeComponent implements OnInit {
  public trainingsData: training[] = [];
  public lang: String | null = "";

  constructor(
    private router: Router,
    private svc: TrainingService,
    private jwtDecoderService: JwtDecoderService,
    public roleAuth: RoleAuthorizationService
  ) { }


  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    const role = this.jwtDecoderService.getUserRole();
    const products: string[] = this.jwtDecoderService.getProducts() || [];

    this.svc.getTrainingData().subscribe(
      (data: training[]) => {
        if (role === "Skipper" || role === "Fleetmanager") {
          if (products && products.length > 0) {
            this.trainingsData = data.filter(training => {
              return products.some(product =>
                training.name_en.toLowerCase().includes(product.toLowerCase())
              );
            });
          } else {
            this.trainingsData = [];
          }
        }
        else if (role === "Administrator" || role === "Support") {
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

  getTrainingName(index: number) {
    if (this.lang == "en") {
      return this.trainingsData[index].name_en
    }
    if (this.lang == "nl") {
      return this.trainingsData[index].name_nl
    }
    if (this.lang == "de") {
      return this.trainingsData[index].name_de
    }
    if (this.lang == "fr") {
      return this.trainingsData[index].name_fr
    } else {
      return "Unknown language"
    }
  }

  getTrainingDescription(index: number) {
    if (this.lang == "en") {
      return this.trainingsData[index].description_en
    }
    if (this.lang == "nl") {
      return this.trainingsData[index].description_nl
    }
    if (this.lang == "de") {
      return this.trainingsData[index].description_de
    }
    if (this.lang == "fr") {
      return this.trainingsData[index].description_fr
    } else {
      return "Unknown language"
    }
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
  name_en: String
  name_nl: String
  name_fr: String
  name_de: String
  description_en: String
  description_nl: String
  description_fr: String
  description_de: String
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
  photos: Photo[]
}

export interface Photo {
  id: number;
  imageData: string;
}
