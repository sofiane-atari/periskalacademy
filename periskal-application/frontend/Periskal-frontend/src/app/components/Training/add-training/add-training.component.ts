import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';
import { VideoUploadComponent } from '../../video-upload/video-upload.component';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { JsonPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-add-training',
  standalone: true,
  imports: [FormsModule,
    ReactiveFormsModule,
    TranslatePipe,
    PhotoUploadComponent,
    VideoUploadComponent,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatCardModule,
    MatExpansionModule,
    MatIconModule,
    MatDividerModule,
    MatListModule],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent implements OnInit { // Implement OnInit
  // Removed separate language forms (enForm, nlForm, etc.)
  moduleCollapsedState: { [lang: string]: { [moduleIndex: number]: boolean } } = {};
  chapterCollapsedState: { [lang: string]: { [moduleIndex: number]: { [chapterIndex: number]: boolean } } } = {};
  selectedLanguage: String = "";

  trainingForm!: FormGroup;
  languages = ['en', 'nl', 'fr', 'de'];

  constructor(private fb: FormBuilder, private trainingService: TrainingService, private router: Router) { }

  ngOnInit() {
    this.trainingForm = this.fb.group({
      productCode: ['', Validators.required],
      nameEn: ['', Validators.required],
      nameNl: ['', Validators.required],
      nameFr: ['', Validators.required],
      nameDe: ['', Validators.required],
      descriptionEn: ['', Validators.required],
      descriptionNl: ['', Validators.required],
      descriptionFr: ['', Validators.required],
      descriptionDe: ['', Validators.required],
      modules: this.fb.array([this.createModule()])
    });

  }

  createModule(): FormGroup {
    return this.fb.group({
      nameModuleEn: ['', Validators.required],
      descriptionModuleEn: ['', Validators.required],
      nameModuleNl: ['', Validators.required],
      descriptionModuleNl: ['', Validators.required],
      nameModuleFr: ['', Validators.required],
      descriptionModuleFr: ['', Validators.required],
      nameModuleDe: ['', Validators.required],
      descriptionModuleDe: ['', Validators.required],
      chapters: this.fb.array([this.createChapter()])
    });
  }


  createChapter(): FormGroup {
    return this.fb.group({
      nameChapterEn: ['', Validators.required],
      descriptionChapterEn: ['', Validators.required],
      textChapterEn: ['', Validators.required],
      photos: [[]],
      video: [null],
      nameChapterNl: ['', Validators.required],
      descriptionChapterNl: ['', Validators.required],
      textChapterNl: ['', Validators.required],
      nameChapterFr: ['', Validators.required],
      descriptionChapterFr: ['', Validators.required],
      textChapterFr: ['', Validators.required],
      nameChapterDe: ['', Validators.required],
      descriptionChapterDe: ['', Validators.required],
      textChapterDe: ['', Validators.required],
    });
  }

  onVideoSelected(file: File): void {
    console.log('test:', file);
  }


  get modules(): FormArray {
    return this.trainingForm.get('modules') as FormArray;
  }

  addModule(): void {
    this.modules.push(this.createModule());
  }

  removeModule(moduleIndex: number): void {
    this.modules.removeAt(moduleIndex);
  }

  getChapters(moduleIndex: number): FormArray {
    return this.modules.at(moduleIndex).get('chapters') as FormArray;
  }

  addChapter(moduleIndex: number): void {
    this.getChapters(moduleIndex).push(this.createChapter());
  }
  removeChapter(moduleIndex: number, chapterIndex: number): void {
    const chapters = (this.trainingForm.get(['modules', moduleIndex, 'chapters']) as FormArray);
    chapters.removeAt(chapterIndex);
  }

  postTraining(): void {
    if (this.trainingForm.valid) { // Removed checks for separate language forms
      const trainingData = this.trainingForm.value;
      const fileInput = this.trainingForm.get('image')?.value; // Assuming 'image' is a control somewhere

      // If there's a file, convert it to base64
      if (fileInput) {
        this.convertToBase64(fileInput).then((base64Image: string) => {
          // Add the base64 image to the trainingData
          trainingData.modules.forEach((module: { chapters: any[]; }) => {
            module.chapters.forEach(chapter => {
              // Assuming 'photos' is an object with language keys
              if (chapter.photos) {
                Object.keys(chapter.photos).forEach(lang => {
                  chapter.photos[lang] = base64Image; // Assign the same image to all languages for simplicity
                });
              }
            });
          });

          // Now send the updated data with base64 image to the backend
          this.trainingService.postTraining(trainingData).subscribe({
            next: (response) => {
              // this.resetForm();
              this.router.navigate(['/trainingEmployee']);
            },
            error: (error) => {
              console.error('Error posting training', error);
            }
          });
        });
      } else {
        this.trainingService.postTraining(trainingData).subscribe({
          next: (response) => {
            // this.resetForm();
            this.router.navigate(['/trainingEmployee']);
          },
          error: (error) => {
            console.error('Error posting training', error);
          }
        });
      }
    } else {
      console.log('Form is invalid');
      // Optionally, trigger visual validation feedback to the user
    }
  }

  // Helper method to convert file to base64
  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        resolve(reader.result as string);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
}