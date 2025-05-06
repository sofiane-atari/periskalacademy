import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';
import { VideoUploadComponent } from '../../video-upload/video-upload.component';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
@Component({
  selector: 'app-add-training',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, TranslatePipe, PhotoUploadComponent, VideoUploadComponent, MatStepperModule, MatFormFieldModule, MatInputModule],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent {
  trainingForm: FormGroup;
  enForm!: FormGroup;
  nlForm!: FormGroup;
  deForm!: FormGroup;
  frForm!: FormGroup;
  moduleCollapsedState: { [lang: string]: { [moduleIndex: number]: boolean } } = {};
  chapterCollapsedState: { [lang: string]: { [moduleIndex: number]: { [chapterIndex: number]: boolean } } } = {};
  selectedLanguage: String = "";
  languages = ['en', 'nl', 'fr', 'de'];

  constructor(private fb: FormBuilder, private router: Router, private trainingsService: TrainingService) {
    this.trainingForm = this.fb.group({
      product_code: ['', Validators.required],
    });
    this.enForm = this.fb.group({
      name_en: ['', Validators.required],
      description_en: ['', Validators.required],
      modules: this.fb.array([]),
    });

    this.languages.forEach(lang => {
      this.moduleCollapsedState[lang] = {};
      this.chapterCollapsedState[lang] = {};
    });
  }

  getModulesControls(lang: string): FormArray {
    switch (lang) {
      case 'en':
        return this.enForm.get('modules') as FormArray;
      case 'nl':
        return this.nlForm.get('modules') as FormArray;
      case 'fr':
        return this.frForm.get('modules') as FormArray;
      case 'de':
        return this.deForm.get('modules') as FormArray;
      default:
        return this.fb.array([]);
    }
  }

  getChaptersControls(lang: string, moduleIndex: number): FormArray {
    const modules = this.getModulesControls(lang);
    if (modules.at(moduleIndex) && modules.at(moduleIndex).get('chapters')) {
      return modules.at(moduleIndex).get('chapters') as FormArray;
    }
    return this.fb.array([]);
  }

  addModule(lang: string) {
    const modules = this.getModulesControls(lang);
    modules.push(this.fb.group({
      nameModule: ['', Validators.required],
      descriptionModule: ['', Validators.required],
      chapters: this.fb.array([])
    }));
    this.moduleCollapsedState[lang][modules.length - 1] = false; // Initially expanded
  }

  // Method to remove a module for a specific language
  removeModule(lang: string, moduleIndex: number) {
    const modules = this.getModulesControls(lang);
    modules.removeAt(moduleIndex);
    delete this.moduleCollapsedState[lang][moduleIndex];
  }

  // Method to add a new chapter to a specific module in a language
  addChapter(lang: string, moduleIndex: number) {
    const chapters = this.getChaptersControls(lang, moduleIndex);
    chapters.push(this.fb.group({
      nameChapter: ['', Validators.required],
      descriptionChapter: ['', Validators.required],
      textChapter: ['', Validators.required],
      photos: [''], // You might need a more complex control for file uploads
      // video: [''] // You might need a more complex control for video uploads
    }));
    if (!this.chapterCollapsedState[lang][moduleIndex]) {
      this.chapterCollapsedState[lang][moduleIndex] = {};
    }
    this.chapterCollapsedState[lang][moduleIndex][chapters.length - 1] = false; // Initially expanded
  }

  // Method to remove a chapter from a specific module in a language
  removeChapter(lang: string, moduleIndex: number, chapterIndex: number) {
    const chapters = this.getChaptersControls(lang, moduleIndex);
    chapters.removeAt(chapterIndex);
    if (this.chapterCollapsedState[lang][moduleIndex]) {
      delete this.chapterCollapsedState[lang][moduleIndex][chapterIndex];
    }
  }

  // Methods to handle module collapse state
  isModuleCollapsed(lang: string, moduleIndex: number): boolean {
    return this.moduleCollapsedState[lang]?.[moduleIndex] || false;
  }

  toggleModule(lang: string, moduleIndex: number) {
    if (!this.moduleCollapsedState[lang]) {
      this.moduleCollapsedState[lang] = {};
    }
    this.moduleCollapsedState[lang][moduleIndex] = !this.moduleCollapsedState[lang][moduleIndex];
  }

  // Methods to handle chapter collapse state
  isChapterCollapsed(lang: string, moduleIndex: number, chapterIndex: number): boolean {
    return this.chapterCollapsedState[lang]?.[moduleIndex]?.[chapterIndex] || false;
  }

  toggleChapter(lang: string, moduleIndex: number, chapterIndex: number) {
    if (!this.chapterCollapsedState[lang]) {
      this.chapterCollapsedState[lang] = {};
    }
    if (!this.chapterCollapsedState[lang][moduleIndex]) {
      this.chapterCollapsedState[lang][moduleIndex] = {};
    }
    this.chapterCollapsedState[lang][moduleIndex][chapterIndex] = !this.chapterCollapsedState[lang][moduleIndex][chapterIndex];
  }

  // Method to get the module title (you might fetch this dynamically or use a default)
  getModuleTitle(lang: string, moduleIndex: number): string {
    const moduleControl = this.getModulesControls(lang).at(moduleIndex) as FormGroup;
    return moduleControl?.get('nameModule')?.value || 'New Module';
  }

  // Method to get the chapter title
  getChapterTitle(lang: string, moduleIndex: number, chapterIndex: number): string {
    const chapterControl = this.getChaptersControls(lang, moduleIndex).at(chapterIndex) as FormGroup;
    return chapterControl?.get('nameChapter')?.value || 'New Chapter';
  }

  postTraining(): void {
    if (this.trainingForm.valid && this.enForm.valid && this.nlForm.valid && this.deForm.valid && this.frForm.valid) {
      const trainingData = {
        product_code: this.trainingForm.value.product_code,
        en: {
          name: this.enForm.value.name,
          description: this.enForm.value.description,
          modules: (this.enForm.get('modules') as FormArray).value,
        },
        nl: {
          name: this.nlForm.value.name,
          description: this.nlForm.value.description,
          modules: (this.nlForm.get('modules') as FormArray).value,
        },
        fr: {
          name: this.frForm.value.name,
          description: this.frForm.value.description,
          modules: (this.frForm.get('modules') as FormArray).value,
        },
        de: {
          name: this.deForm.value.name,
          description: this.deForm.value.description,
          modules: (this.deForm.get('modules') as FormArray).value,
        },
      };

      const fileInput = this.trainingForm.get('image')?.value;

      // If there's a file, convert it to base64
      if (fileInput) {
        this.convertToBase64(fileInput).then((base64Image: string) => {
          // Add the base64 image to the trainingData
          trainingData.en.modules.forEach((module: { chapters: any[]; }) => {
            module.chapters.forEach(chapter => {
              chapter.photos.forEach((photo: { imageData: string; }) => {
                photo.imageData = base64Image;
              });
            });
          });

          // Now send the updated data with base64 image to the backend
          this.trainingsService.postTraining(trainingData).subscribe({
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
        this.trainingsService.postTraining(trainingData).subscribe({
          next: (response) => {
            // this.resetForm();
            this.router.navigate(['/trainingEmployee']);
          },
          error: (error) => {
            console.error('Error posting training', error);
          }
        });

      }
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


  // resetForm(): void {
  //   this.trainingForm.reset();

  //   while (this.modules.length) {
  //     this.modules.removeAt(0);
  //   }
  //   this.addModule();

  //   // Reset alle collapse states
  //   this.collapsedModules = {};
  //   this.collapsedChapters = {};
  // }
}