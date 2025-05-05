import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ModuleDTO } from '../add-module/add-module.component';
import { ModuleService } from '../../../services/Module/module.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-add-chapter',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    TranslatePipe
  ],
  templateUrl: './add-chapter.component.html',
  styleUrl: './add-chapter.component.css'
})
export class AddChapterComponent implements OnInit {
  moduleId: number = 0;
  trainingId: number = 0;
  enForm!: FormGroup;
  nlForm!: FormGroup;
  frForm!: FormGroup;
  deForm!: FormGroup;

  constructor(private fb: FormBuilder, private moduleService: ModuleService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.trainingId = Number(this.route.snapshot.paramMap.get('trainingid'));
    this.moduleId = Number(this.route.snapshot.paramMap.get('moduleid'));
    this.enForm = this.fb.group({
      nameChapter_en: ['', Validators.required],
      descriptionChapter_en: ['', Validators.required],
      textChapter_en: ['', Validators.required],
    });

    this.nlForm = this.fb.group({
      nameChapter_nl: ['', Validators.required],
      descriptionChapter_nl: ['', Validators.required],
      textChapter_nl: ['', Validators.required],
    });

    this.frForm = this.fb.group({
      nameChapter_fr: ['', Validators.required],
      descriptionChapter_fr: ['', Validators.required],
      textChapter_fr: ['', Validators.required],
    });

    this.deForm = this.fb.group({
      nameChapter_de: ['', Validators.required],
      descriptionChapter_de: ['', Validators.required],
      textChapter_de: ['', Validators.required],
    });
  }

  postModule(): void {
    if (this.enForm.valid && this.nlForm.valid && this.frForm.valid && this.deForm.valid) {
      const chapterData: ChapterDTO = {
        ...this.enForm.value,
        ...this.nlForm.value,
        ...this.frForm.value,
        ...this.deForm.value,
        id: null
      };
      this.moduleService.AddChapterToModule(this.moduleId, chapterData)
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
}

export interface ChapterDTO {
  id: number | null;
  nameChapter_en: string;
  descriptionChapter_en: string;
  textChapter_en: string;
  nameChapter_nl: string;
  descriptionChapter_nl: string;
  textChapter_nl: string;
  nameChapter_fr: string;
  descriptionChapter_fr: string;
  textChapter_fr: string;
  nameChapter_de: string;
  descriptionChapter_de: string;
  textChapter_de: string;
}
