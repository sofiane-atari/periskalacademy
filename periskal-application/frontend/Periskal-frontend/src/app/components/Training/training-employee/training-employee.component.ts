import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { RoleAuthorizationService } from '../../../services/RoleAuthorization/role-authorization.service';
import { Training } from '../../../models/training.model';


@Component({
  selector: 'app-training-employee',
  imports: [TranslatePipe],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})

export class TrainingEmployeeComponent implements OnInit {
  public trainingsData: Training[] = [];
  public lang: String | null = "";
  public openMenus: { [key: number]: boolean } = {};
  public menuOpenAdd: boolean = false;

  constructor(
    private router: Router,
    private trainingSvc: TrainingService,
    private jwtDecoderService: JwtDecoderService,
    public roleAuth: RoleAuthorizationService
  ) { }


  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    const role = this.jwtDecoderService.getUserRole() || "noRole";
    const products: string[] = this.jwtDecoderService.getProducts() || [];

    // Use filtered endpoint instead of fetching all trainings and filtering client-side
    this.trainingSvc.getFilteredTrainingData(role, products).subscribe({
      next: (data: Training[]) => {
        this.trainingsData = data;
      },
      error: (error) => {
        console.error('Error fetching trainings', error);
      }
    });
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
  toggleMenuEdit(trainingId: number) {
    this.openMenus[trainingId] = !this.openMenus[trainingId];
  }
  
  toggleMenuAdd() {
    this.menuOpenAdd = !this.menuOpenAdd;
  }

  goToAddTrainingPage() {
    this.router.navigate(['/add-training'])
  }
  goToEditTrainingPage(trainingId: number) {
    this.router.navigate(['/edit', trainingId])
  }
  goToAddExamPage() {
    this.router.navigate(['/add-exam'])
  }
  goToEditExamPage() {
    this.router.navigate(['/edit'])
  }
}



