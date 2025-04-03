import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { Module } from '../../../models/module.model';

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe],
  templateUrl: './training-detail.component.html',
  styleUrl: './training-detail.component.css'
})
export class TrainingDetailComponent {
  training: Training | undefined;
  modules: Module[] = [];
  public lang: String | null = "";

  trainingId: number = 0;

  constructor(private route: ActivatedRoute, private trainingSvc: TrainingService, private router: Router) {
  }

  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));

    // Call the service method to get the products for the specific training
    this.trainingSvc.getTrainingById(this.trainingId).subscribe({
      next: (data) => {
        this.training = data;
        this.modules = data.modules;
      },
      error: (error) => {
        console.error('Error fetching training products', error);
      }
    });

  }

  goToChaptersPage(moduleId: number) {
    this.router.navigate(['/training', this.trainingId, 'module', moduleId])
  }

  getModuleTitle(moduleId: number) {
    if (this.lang == "en") {
      return this.training?.modules[moduleId].nameModule_en;
    }
    if (this.lang == "nl") {
      return this.training?.modules[moduleId].nameModule_nl;
    }
    if (this.lang == "fr") {
      return this.training?.modules[moduleId].nameModule_fr;
    }
    if (this.lang == "de") {
      return this.training?.modules[moduleId].nameModule_de;
    }
    else {
      return "Unknown language"
    }
  }

  getModuleDescription(moduleId: number) {
    if (this.lang == "en") {
      return this.training?.modules[moduleId].descriptionModule_en;
    }
    if (this.lang == "nl") {
      return this.training?.modules[moduleId].descriptionModule_nl;
    }
    if (this.lang == "fr") {
      return this.training?.modules[moduleId].descriptionModule_fr;
    }
    if (this.lang == "de") {
      return this.training?.modules[moduleId].descriptionModule_de;
    }
    else {
      return "Unknown language"
    }
  }
}
