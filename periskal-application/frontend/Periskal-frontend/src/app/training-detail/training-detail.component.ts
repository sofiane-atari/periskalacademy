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

  constructor(private route: ActivatedRoute, private svc: TrainingService) {
    this.trainingSvc = svc;
  }

  // ngOnInit(): void {
  //   const trainingId = +this.route.snapshot.paramMap.get('id')!;
  //   const result = this.trainingSvc.getProductsData(trainingId);
  //   if(result != null)
  //     this.products = result;
  //   else
  //     alert("No products found");
  // }
}
