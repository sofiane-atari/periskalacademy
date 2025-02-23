import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { product } from '../training-employee/training-employee.component';

@Component({
  selector: 'app-add-training',
  imports: [FormsModule],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent {
  name : String = "";
  description : String = "";
  products : product[] = [];

  constructor() {
    this.products.push();
  }
  AddProduct() {
    this.products.push();
    console.log(this.products);
  }
}
