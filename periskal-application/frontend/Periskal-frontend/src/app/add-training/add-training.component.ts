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
  index : number = 0;
  products : number[] = [];

  nameModule : String = "";
  descriptionModule : String = "";

  constructor() {
    this.products.push(this.index++);
  }
  AddProduct() {
    this.products.push(this.index++);
    console.log(this.products);
  }
  RemoveProduct() {
    if(this.index===0) {
      this.index = 0;
    } else {
      this.index--;
      this.products.pop();
    }
  }
}
