import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, Validators, ReactiveFormsModule } from '@angular/forms';
import { module } from '../training-employee/training-employee.component';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { TrainingService } from '../training.service';

@Component({
  selector: 'app-add-training',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './add-training.component.html',
  styleUrl: './add-training.component.css'
})
export class AddTrainingComponent {
  trainingForm: FormGroup;
  collapsedModules: { [key: number]: boolean } = {};
  collapsedChapters: { [key: string]: boolean } = {};

  constructor(private fb: FormBuilder, private client: HttpClient, private router: Router, private trainingsService: TrainingService) {
    this.trainingForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      modules: this.fb.array([this.createModule()])
    });
  }

  get modules(): FormArray {
    return this.trainingForm.get('modules') as FormArray;
  }

  getChaptersControls(moduleIndex: number): any[] {
    return (this.modules.at(moduleIndex).get('chapters') as FormArray).controls;
  }

  createModule(): FormGroup {
    return this.fb.group({
      nameModule: ['', Validators.required],
      descriptionModule: ['', Validators.required],
      chapters: this.fb.array([this.createChapter()])
    });
  }

  createChapter(): FormGroup {
    return this.fb.group({
      nameChapter: ['', Validators.required],
      descriptionChapter: ['', Validators.required],
      textChapter: ['', Validators.required]
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

    while (this.modules.length) {
      this.modules.removeAt(0);
    }
    this.addModule();

    // Reset alle collapse states
    this.collapsedModules = {};
    this.collapsedChapters = {};
  }
}