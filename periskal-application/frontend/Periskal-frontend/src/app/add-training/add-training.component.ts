import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { product } from '../training-employee/training-employee.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-add-training',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent implements OnInit {

  trainingForm!: FormGroup;

  constructor(private fb : FormBuilder, private http: HttpClient) {};

  ngOnInit(): void {
      this.trainingForm = this.fb.group({
        name: ['', Validators.required],
        description: ['',Validators.required],
        modules: this.fb.array([]),
      });
      this.addModule();
  }

  get modules() {
    return this.trainingForm.get('modules') as FormArray;
  }

  addModule() {
    const moduleGroup = this.fb.group({
      nameModule: ['', Validators.required],
      descriptionModule: ['', Validators.required],
    });
    this.modules.push(moduleGroup);
  }

  removeModule(index: number) {
    this.modules.removeAt(index);
  }

  postTraining() {
    const trainingData = this.trainingForm.value;

    this.http.post('http://localhost:8080/api/training/create', trainingData).subscribe(
      (response) => {
        console.log('Training posted successfully!', response);
      },
      (error) => {
        console.error('Error posting training', error);
      }
    );
  }
}
