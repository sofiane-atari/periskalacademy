import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';

@Component({
  selector: 'app-add-module',
  templateUrl: './add-module.component.html',
  imports: [ReactiveFormsModule],
  styleUrls: ['./add-module.component.css']
})
export class AddModuleComponent implements OnInit {
  moduleForm: FormGroup;
  trainingId: number = 0;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private trainingService: TrainingService
  ) {
    this.moduleForm = this.fb.group({
      nameModule: ['', Validators.required],
      descriptionModule: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.trainingId = +this.route.snapshot.url[1].path;
  }

  postModule(): void {
    if (this.moduleForm.valid) {
      const moduleData: ModuleDTO = {
        id: null,
        nameModule: this.moduleForm.get('nameModule')?.value,
        descriptionModule: this.moduleForm.get('descriptionModule')?.value
      };

      this.trainingService.addModuleToTraining(this.trainingId, moduleData)
        .subscribe({
          next: (response) => {
            this.router.navigate(['edit', this.trainingId]);
          },
          error: (error) => {
            console.error('Error adding module', error.error);
            console.error('Error status', error.status);
            console.error('Error message', error.message);
          }
        });
    }
  }

  resetForm(): void {
    this.moduleForm.reset({
      title: '',
      description: ''
    });
  }
}
export interface ModuleDTO {
  id: number | null;
  nameModule: string;
  descriptionModule: string;
}