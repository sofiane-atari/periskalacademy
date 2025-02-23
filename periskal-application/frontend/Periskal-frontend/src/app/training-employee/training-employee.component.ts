import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-training-employee',
  imports: [],
  templateUrl: './training-employee.component.html',
  styleUrl: './training-employee.component.css'
})
export class TrainingEmployeeComponent {
  public demoTrainingData: training[] = [
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
  

  constructor(private router: Router) {}

  goToTrainingDetails(trainingId: number) {
    this.router.navigate(['/training', trainingId]);
  }
}

export interface training {
  id: number
  name: String
  description: String
  Products: product[]
}
export interface product {
  id: number
  name: String
  description: String
}
