import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { Chapter } from '../../../models/chapter.model';
import { Module } from '../../../models/module.model';
import { CommonModule } from '@angular/common';
import { CheckboxRequiredValidator } from '@angular/forms';

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe, CommonModule],
  templateUrl: './module-detail.component.html',
  styleUrl: './module-detail.component.css'
})
export class ModuleDetailComponent {

  // chapters: Chapter[] = [
  //   {
  //     id: 1,
  //     nameChapter_en: 'Introduction to Angular',
  //     nameChapter_nl: 'Introductie tot Angular',
  //     nameChapter_fr: 'Introduction à Angular',
  //     nameChapter_de: 'Einführung in Angular',
  //     descriptionChapter_en: 'Learn the basics',
  //     descriptionChapter_de: 'duits',
  //     descriptionChapter_fr: 'frans',
  //     descriptionChapter_nl: "nederlands",
  //     textChapter_de: 'duits',
  //     textChapter_fr: 'frans',
  //     textChapter_nl: 'nederlands',
  //     textChapter_en: 'Angular is a platform...',
  //     photos: []
  //   },
  //   {
  //     id: 2,
  //     nameChapter_en: 'Components',
  //     nameChapter_nl: 'Componenten',
  //     nameChapter_fr: 'Composants',
  //     nameChapter_de: 'Komponenten',
  //     descriptionChapter_de: 'duits',
  //     descriptionChapter_fr: 'frans',
  //     descriptionChapter_nl: "nederlands",
  //     textChapter_de: 'duits',
  //     textChapter_fr: 'frans',
  //     textChapter_nl: 'nederlands',
  //     descriptionChapter_en: 'Building blocks of Angular',
  //     textChapter_en: 'Components are the main building blocks...',
  //     photos: []
  //   }
  // ];

  training: Training | undefined;
  modules: Module[] = [];
  text: string = "";
  chapters: Chapter[] = [];
  public lang: String | null = "";
  updatedChapters: Chapter[] = [];

  trainingId: number = 0;
  moduleId: number = 0;
  activeChapterId: number = 0;

  constructor(private route: ActivatedRoute, private trainingSvc: TrainingService, private router: Router, private cdr: ChangeDetectorRef) {
  }


  formatText(content: string): string {
    if (!content) return 'No content available';
    return content.replace(/\n/g, '<br>');
  }

  ngOnInit(): void {

    this.lang = localStorage.getItem('lang');
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    this.moduleId = Number(this.route.snapshot.paramMap.get('moduleid'))

    this.loadTrainingData();
  }

  getChapterArray(chapters: any): any[] {
    // Convert object-like structure to array
    if (Array.isArray(chapters)) return chapters;
    if (chapters instanceof Object) return Object.values(chapters);
    return [];
  }

  ngAfterViewChecked() {
    console.log('Chapters:', this.chapters);
  }

  private loadTrainingData(): void {
    this.trainingSvc.getTrainingById(this.trainingId).subscribe({
      next: (data) => {
        console.log(data);
        this.training = data;
        console.log(this.training);
        this.modules = this.training.modules || []; // Ensure modules is never undefined
        console.log(this.modules);

        // Safely find the module and its chapter
        this.chapters = this.modules[this.moduleId - 1].chapters;
        console.log(this.chapters[0].nameChapterEn)
        console.log(this.chapters[1].nameChapterEn)
        console.log('Loaded chapters:', this.chapters);
        this.chapters.forEach((e) => this.updatedChapters.push(e));
      },
      error: (error) => {
        console.error('Error loading training data', error);
        this.cdr.detectChanges();
      }
    });
  }

  goToChaptersPage(moduleId: number) {
    this.router.navigate(['/training', this.trainingId, 'module', moduleId])
  }

  setActiveChapter(chapterId: number) {
    this.activeChapterId = chapterId;
  }

  getChapterName(chapterId: number) {
    if (this.lang == "en") {
      return this.chapters[chapterId].nameChapterEn;
    }
    if (this.lang == "nl") {
      return this.chapters[chapterId].nameChapterNl;
    }
    if (this.lang == "fr") {
      return this.chapters[chapterId].nameChapterFr;
    }
    if (this.lang == "de") {
      return this.chapters[chapterId].nameChapterDe;
    }
    return "Unknown language"
  }

  getActiveChapterName() {
    if (this.lang == "en") {
      return this.chapters[this.activeChapterId].nameChapterEn;
    }
    if (this.lang == "nl") {
      return this.chapters[this.activeChapterId].nameChapterNl;
    }
    if (this.lang == "fr") {
      return this.chapters[this.activeChapterId].nameChapterFr;
    }
    if (this.lang == "de") {
      return this.chapters[this.activeChapterId].nameChapterDe;
    }
    return "Unknown language"
  }

  getActiveChapterDescription() {
    if (this.lang == "en") {
      return this.chapters[this.activeChapterId].descriptionChapterEn;
    }
    if (this.lang == "nl") {
      return this.chapters[this.activeChapterId].descriptionChapterNl;
    }
    if (this.lang == "fr") {
      return this.chapters[this.activeChapterId].descriptionChapterFr;
    }
    if (this.lang == "de") {
      return this.chapters[this.activeChapterId].descriptionChapterDe;
    }
    return "Unknown language"
  }
}
