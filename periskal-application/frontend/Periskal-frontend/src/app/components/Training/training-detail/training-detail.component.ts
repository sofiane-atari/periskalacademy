import { Component, OnInit } from '@angular/core';
import { module, training } from '../training-employee/training-employee.component';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe],
  templateUrl: './training-detail.component.html',
  styleUrl: './training-detail.component.css'
})
export class TrainingDetailComponent {
  training: training | undefined;
  modules: module[] = [];

  trainingSvc: TrainingService;

  trainingId: number = 0;

  constructor(private route: ActivatedRoute, private svc: TrainingService, private router: Router) {
    this.trainingSvc = svc;
  }

  ngOnInit(): void {
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));

    // Call the service method to get the products for the specific training
    this.trainingSvc.getTrainingById(this.trainingId).subscribe(
      (data) => {
        this.training = data;
        this.modules = data.modules
        console.log(this.training.modules);
      },
      (error) => {
        console.error('Error fetching training products', error);
      }
    );
  }

  goToChaptersPage(moduleId: number) {
    this.router.navigate(['/training', this.trainingId, 'module', moduleId])
  }
}
