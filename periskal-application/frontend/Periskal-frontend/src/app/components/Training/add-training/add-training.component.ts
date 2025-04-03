import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { PhotoUploadComponent } from '../../photo-upload/photo-upload.component';

@Component({
  selector: 'app-add-training',
  imports: [FormsModule, ReactiveFormsModule, TranslatePipe, PhotoUploadComponent],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent {
  trainingForm: FormGroup;
  collapsedModules: { [key: number]: boolean } = {};
  collapsedChapters: { [key: string]: boolean } = {};
  selectedLanguage: String = "";
  languages = ['en', 'nl', 'fr', 'de'];

  constructor(private fb: FormBuilder, private router: Router, private trainingsService: TrainingService) {
    this.trainingForm = this.fb.group({
      product_code: ['', Validators.required],
      name_en: ['', Validators.required],
      name_nl: ['', Validators.required],
      name_fr: ['', Validators.required],
      name_de: ['', Validators.required],
      description_en: ['', Validators.required],
      description_nl: ['', Validators.required],
      description_fr: ['', Validators.required],
      description_de: ['', Validators.required],
      modules: this.fb.array([this.createModule()])
    });
  }

  onLanguageChange(event: any) {
    this.selectedLanguage = event.target.value;
  }

  get modules(): FormArray {
    return this.trainingForm.get('modules') as FormArray;
  }

  getChaptersControls(moduleIndex: number): any[] {
    return (this.modules.at(moduleIndex).get('chapters') as FormArray).controls;
  }

  createModule(): FormGroup {
    return this.fb.group({
      nameModule_en: ['', Validators.required],
      descriptionModule_en: ['', Validators.required],
      nameModule_nl: ['', Validators.required],
      descriptionModule_nl: ['', Validators.required],
      nameModule_fr: ['', Validators.required],
      descriptionModule_fr: ['', Validators.required],
      nameModule_de: ['', Validators.required],
      descriptionModule_de: ['', Validators.required],
      chapters: this.fb.array([this.createChapter()])
    });
  }

  createChapter(): FormGroup {
    return this.fb.group({
      nameChapter_en: ['', Validators.required],
      descriptionChapter_en: ['', Validators.required],
      textChapter_en: ['', Validators.required],
      photos_en: [[]],
      nameChapter_nl: ['', Validators.required],
      descriptionChapter_nl: ['', Validators.required],
      textChapter_nl: ['', Validators.required],
      photos_nl: [[]],
      nameChapter_fr: ['', Validators.required],
      descriptionChapter_fr: ['', Validators.required],
      textChapter_fr: ['', Validators.required],
      photos_fr: [[]],
      nameChapter_de: ['', Validators.required],
      descriptionChapter_de: ['', Validators.required],
      textChapter_de: ['', Validators.required],
      photos_de: [[]]
    });
  }

  addModule(): void {
    const newIndex = this.modules.length;
    this.modules.push(this.createModule());
    //standaard nieuwe module uitgeklapt tonen
    this.collapsedModules[newIndex] = false;
  }

  removeModule(index: number): void {
    this.modules.removeAt(index);
  }

  addChapter(moduleIndex: number): void {
    const chapters = this.modules.at(moduleIndex).get('chapters') as FormArray;
    const newChapterIndex = chapters.length;
    chapters.push(this.createChapter());
    //standaard nieuw hoofdstuk uitgeklapt tonen
    this.collapsedChapters[`${moduleIndex}-${newChapterIndex}`] = false;
  }

  removeChapter(moduleIndex: number, chapterIndex: number): void {
    const chapters = this.modules.at(moduleIndex).get('chapters') as FormArray;
    chapters.removeAt(chapterIndex);

    //verwijder de collapse state voor dit hoofdstuk
    delete this.collapsedChapters[`${moduleIndex}-${chapterIndex}`];

    //herorganiseer de indices van de overgebleven hoofdstukken
    const newCollapsedChapters: { [key: string]: boolean } = {};
    Object.keys(this.collapsedChapters).forEach(key => {
      const [modIdx, chapIdx] = key.split('-').map(Number);
      if (modIdx === moduleIndex && chapIdx > chapterIndex) {
        newCollapsedChapters[`${modIdx}-${chapIdx - 1}`] = this.collapsedChapters[key];
      } else if (modIdx === moduleIndex && chapIdx < chapterIndex) {
        newCollapsedChapters[key] = this.collapsedChapters[key];
      } else {
        newCollapsedChapters[key] = this.collapsedChapters[key];
      }
    });
    this.collapsedChapters = newCollapsedChapters;
  }

  toggleModule(index: number): void {
    this.collapsedModules[index] = !this.isModuleCollapsed(index);
  }

  isModuleCollapsed(index: number): boolean {
    return this.collapsedModules[index] === true;
  }

  toggleChapter(moduleIndex: number, chapterIndex: number): void {
    const key = `${moduleIndex}-${chapterIndex}`;
    this.collapsedChapters[key] = !this.isChapterCollapsed(moduleIndex, chapterIndex);
  }

  isChapterCollapsed(moduleIndex: number, chapterIndex: number): boolean {
    const key = `${moduleIndex}-${chapterIndex}`;
    return this.collapsedChapters[key] === true;
  }

  getModuleTitle(index: number): string {
    const moduleControl = this.modules.at(index);
    const title = moduleControl.get('nameModule')?.value;
    return title ? title : 'Nieuwe module';
  }

  getChapterTitle(moduleIndex: number, chapterIndex: number): string {
    const chapters = this.modules.at(moduleIndex).get('chapters') as FormArray;
    const chapterControl = chapters.at(chapterIndex);
    const title = chapterControl.get('nameChapter')?.value;
    return title ? title : 'Nieuw hoofdstuk';
  }

  postTraining(): void {
    if (this.trainingForm.valid) {
      const trainingData = this.trainingForm.value;

      const fileInput = this.trainingForm.get('image')?.value;

      // If there's a file, convert it to base64
      if (fileInput) {
        this.convertToBase64(fileInput).then((base64Image: string) => {
          // Add the base64 image to the trainingData
          trainingData.modules.forEach((module: { chapters: any[]; }) => {
            module.chapters.forEach(chapter => {
              chapter.photos.forEach((photo: { imageData: string; }) => {
                photo.imageData = base64Image;
              });
            });
          });

          // Now send the updated data with base64 image to the backend
          this.trainingsService.postTraining(trainingData).subscribe({
            next: (response) => {
              this.resetForm();
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
            this.resetForm();
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


  resetForm(): void {
    this.trainingForm.reset();

    while (this.modules.length) {
      this.modules.removeAt(0);
    }
    this.addModule();

    // Reset alle collapse states
    this.collapsedModules = {};
    this.collapsedChapters = {};
  }
}