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

  getModuleTitle() {
    if (this.lang == "en") {
      return this.training?.name_en;
    }
    if (this.lang == "nl") {
      return this.training?.name_nl;
    }
    if (this.lang == "fr") {
      return this.training?.name_fr;
    }
    if (this.lang == "de") {
      return this.training?.name_de;
    }
    else {
      return "Unknown language"
    }
  }

  getModuleDescription() {
    if (this.lang == "en") {
      return this.training?.description_en;
    }
    if (this.lang == "nl") {
      return this.training?.description_nl;
    }
    if (this.lang == "fr") {
      return this.training?.description_fr;
    }
    if (this.lang == "de") {
      return this.training?.description_de;
    }
    else {
      return "Unknown language"
    }
  }
}
