import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-add-module',
  templateUrl: './add-module.component.html',
  styleUrls: ['./add-module.component.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    TranslatePipe
  ]
})
export class AddModuleComponent implements OnInit {
  enForm!: FormGroup;
  nlForm!: FormGroup;
  frForm!: FormGroup;
  deForm!: FormGroup;
  trainingId = 0;

  constructor(private fb: FormBuilder, private trainingService: TrainingService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.trainingId = +this.route.snapshot.url[1].path;
    this.enForm = this.fb.group({
      nameModuleEn: ['', Validators.required],
      descriptionModuleEn: ['', Validators.required]
    });

    this.nlForm = this.fb.group({
      nameModuleNl: ['', Validators.required],
      descriptionModuleNl: ['', Validators.required]
    });

    this.frForm = this.fb.group({
      nameModuleFr: ['', Validators.required],
      descriptionModuleFr: ['', Validators.required]
    });

    this.deForm = this.fb.group({
      nameModuleDe: ['', Validators.required],
      descriptionModuleDe: ['', Validators.required]
    });
  }

  postModule(): void {
    if (this.enForm.valid && this.nlForm.valid && this.frForm.valid && this.deForm.valid) {
      const moduleData: ModuleDTO = {
        ...this.enForm.value,
        ...this.nlForm.value,
        ...this.frForm.value,
        ...this.deForm.value,
        id: null
      };
      this.trainingService.addModuleToTraining(this.trainingId, moduleData)
        .subscribe({
          next: (response) => {
            this.router.navigate(['edit', this.trainingId]);
          }
        });

    }
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

