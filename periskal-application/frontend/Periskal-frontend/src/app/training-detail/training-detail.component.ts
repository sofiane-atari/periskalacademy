import { Component, OnInit } from '@angular/core';
import { product, training } from '../training-employee/training-employee.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-training-detail',
  imports: [],
  templateUrl: './training-detail.component.html',
  styleUrl: './training-detail.component.css'
})
export class TrainingDetailComponent implements OnInit {
  training: training | undefined;
  products : product[] = [];

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    const trainingId = +this.route.snapshot.paramMap.get('id')!;
    this.loadTrainingDetails(trainingId);
  }

  loadTrainingDetails(trainingId : number) {
    const demoTrainingData: training[] = [
      {
        id: 1,
        name: "Product management",
        description: "Introduction to Product Management",
        Products: [
          {
            id: 1,
            name: "Product management",
            description: "Product Management 101 Book",
          },
          {
            id: 2,
            name: "Roadmap template",
            description: "Product Roadmap Template",
          },
        ],
      },
      {
        id: 2,
        name: "Marketing Strategies",
        description: "Advanced Marketing Strategies",
        Products: [
          {
            id: 3,
            name: "Marketing Campaign intro",
            description: "Marketing Campaign Planner",
          },
          {
            id: 4,
            name: "SEO mastery guide",
            description: "SEO Mastery Guide",
          },
        ],
      },
      {
        id: 3,
        name: "Design thinking",
        description: "Design Thinking Fundamentals",
        Products: [
          {
            id: 5,
            name: "Toolkit",
            description: "Design Thinking Toolkit",
          },
          {
            id: 6,
            name: "Empathy",
            description: "Empathy Map Template",
          },
        ],
      },
    ];
    this.training = demoTrainingData.find((t) => t.id === trainingId);
    if(this.training != null)
      this.products = this.training.Products;
  }
}
