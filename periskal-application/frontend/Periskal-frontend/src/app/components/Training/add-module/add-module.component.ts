import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-add-module',
  templateUrl: './add-module.component.html',
  imports: [ReactiveFormsModule, TranslatePipe],
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
      nameModule_en: ['', Validators.required],
      descriptionModule_en: ['', Validators.required],
      nameModule_nl: ['', Validators.required],
      descriptionModule_nl: ['', Validators.required],
      nameModule_fr: ['', Validators.required],
      descriptionModule_fr: ['', Validators.required],
      nameModule_de: ['', Validators.required],
      descriptionModule_de: ['', Validators.required],

    });
  }

  ngOnInit() {
    this.trainingId = +this.route.snapshot.url[1].path;
  }

  postModule(): void {
    if (this.moduleForm.valid) {
      const moduleData: ModuleDTO = {
        id: null,
        nameModule_en: this.moduleForm.get('nameModule_en')?.value,
        descriptionModule_en: this.moduleForm.get('descriptionModule_en')?.value,
        nameModule_nl: this.moduleForm.get('nameModule_nl')?.value,
        descriptionModule_nl: this.moduleForm.get('descriptionModule_nl')?.value,
        nameModule_fr: this.moduleForm.get('nameModule_fr')?.value,
        descriptionModule_fr: this.moduleForm.get('descriptionModule_fr')?.value,
        nameModule_de: this.moduleForm.get('nameModule_de')?.value,
        descriptionModule_de: this.moduleForm.get('descriptionModule_de')?.value
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
  nameModule_en: string;
  descriptionModule_en: string;
  nameModule_nl: string;
  descriptionModule_nl: string;
  nameModule_fr: string;
  descriptionModule_fr: string;
  nameModule_de: string;
  descriptionModule_de: string;
}