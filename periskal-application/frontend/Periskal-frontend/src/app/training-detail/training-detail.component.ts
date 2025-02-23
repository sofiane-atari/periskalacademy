import { Component, OnInit } from '@angular/core';
import { product, training } from '../training-employee/training-employee.component';
import { ActivatedRoute } from '@angular/router';
import { TrainingService } from '../training.service';

@Component({
  selector: 'app-training-detail',
  imports: [],
  templateUrl: './training-detail.component.html',
  styleUrl: './training-detail.component.css'
})
export class TrainingDetailComponent {
  training: training | undefined;
  products : product[] = [];

  trainingSvc : TrainingService;

  trainingId : number = 0;

  constructor(private route: ActivatedRoute, private svc: TrainingService) {
    this.trainingSvc = svc;
  }

  ngOnInit(): void {
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));

    // Call the service method to get the products for the specific training
    this.trainingSvc.getProductsData(this.trainingId).subscribe(
      (data) => {
        this.products = data;  // Store the fetched data (training and its products)
      },
      (error) => {
        console.error('Error fetching training products', error);
      }
    );
  }
}
