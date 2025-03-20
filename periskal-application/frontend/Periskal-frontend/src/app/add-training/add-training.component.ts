import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { module } from '../training-employee/training-employee.component';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-add-training',
  imports: [FormsModule, ReactiveFormsModule, TranslatePipe],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent {
  trainingForm: FormGroup;

  constructor(private fb: FormBuilder, private client: HttpClient, private router : Router, private trainingsService : TrainingService) {
    this.trainingForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      modules: this.fb.array([this.createModule()])
    });
  }

  get modules(): FormArray {
    return this.trainingForm.get('modules') as FormArray;
  }

  createModule(): FormGroup {
    return this.fb.group({
      nameModule: ['', Validators.required],
      descriptionModule: ['', Validators.required]
    });
  }

  addModule(): void {
    this.modules.push(this.createModule());
  }

  removeModule(index: number): void {
    this.modules.removeAt(index);
  }

  postTraining(): void {
    if (this.trainingForm.valid) {
      const trainingData = this.trainingForm.value;
      console.log(trainingData);
      this.trainingsService.postTraining(trainingData).subscribe(
        (response) => {
          console.log('Training posted successfully!', response);
          this.resetForm();
          this.router.navigate(['/trainingEmployee'])
        },
        (error) => {
          console.error('Error posting training', error);
        }
      );
  
    }
  }
  
  resetForm(): void {
    this.trainingForm.reset();

    while(this.modules.length) {
      this.modules.removeAt(0);
    }
    this.addModule();
  }
}