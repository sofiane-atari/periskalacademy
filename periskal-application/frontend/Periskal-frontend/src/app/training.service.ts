import { Injectable } from '@angular/core';
import { product, training } from './training-employee/training-employee.component';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
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

  constructor() { }

  getTrainingData() : training[] {
    return this.demoTrainingData;
  }
  getProductsData(trainingId : number) : product[] | null {
    const training = this.demoTrainingData.find((t) => t.id === trainingId);
    if(training)
      return training?.Products;
    else
      return null;
  }
}
