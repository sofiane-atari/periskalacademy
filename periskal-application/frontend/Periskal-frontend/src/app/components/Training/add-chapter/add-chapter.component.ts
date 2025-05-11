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
      nameChapterEn: ['', Validators.required],
      descriptionChapterEn: ['', Validators.required],
      textChapterEn: ['', Validators.required],
    });

    this.nlForm = this.fb.group({
      nameChapterNl: ['', Validators.required],
      descriptionChapterNl: ['', Validators.required],
      textChapterNl: ['', Validators.required],
    });

    this.frForm = this.fb.group({
      nameChapterFr: ['', Validators.required],
      descriptionChapterFr: ['', Validators.required],
      textChapterFr: ['', Validators.required],
    });

    this.deForm = this.fb.group({
      nameChapterDe: ['', Validators.required],
      descriptionChapterDe: ['', Validators.required],
      textChapterDe: ['', Validators.required],
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
  nameChapterEn: string;
  descriptionChapterEn: string;
  textChapterEn: string;
  nameChapterNl: string;
  descriptionChapterNl: string;
  textChapterNl: string;
  nameChapterFr: string;
  descriptionChapterFr: string;
  textChapterFr: string;
  nameChapterDe: string;
  descriptionChapterDe: string;
  textChapterDe: string;
}
