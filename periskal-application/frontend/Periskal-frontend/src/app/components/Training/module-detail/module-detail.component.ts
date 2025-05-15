import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { Chapter } from '../../../models/chapter.model';
import { Module } from '../../../models/module.model';
import { CommonModule } from '@angular/common';
import { CheckboxRequiredValidator } from '@angular/forms';
import { Video } from '../../../models/video.model';
import { ModuleProgress, TrainingProgress } from '../../../models/progress.model';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { ProgressService } from '../../../services/Progress/progress.service';
import { AddChapterComponent } from "../add-chapter/add-chapter.component";

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe, CommonModule, AddChapterComponent],
  templateUrl: './module-detail.component.html',
  styleUrl: './module-detail.component.css'
})
export class ModuleDetailComponent {
  training: Training | undefined;
  modules: Module[] = [];
  text: string = "";
  chapters: Chapter[] = [];
  public lang: String | null = "";
  updatedChapters: Chapter[] = [];

  mimeType: string = 'video/mp4';
  video: any;
  errorMessage = "";

  trainingId: number = 0;
  moduleId: number = 0;
  activeChapterId: number = -1;

  //progress
  private userId: string = '';
  trainingProgress: TrainingProgress | undefined;
  moduleProgress: ModuleProgress | undefined;
  isCompletingChapter: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private trainingSvc: TrainingService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private jwtDecoderService: JwtDecoderService,
    private progressService: ProgressService) {
  }


  formatText(content: string | undefined): string {
    if (!content) return 'No content available';
    return content.replace(/\n/g, '<br>');
  }


  ngOnInit(): void {

    this.lang = localStorage.getItem('lang');
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    this.moduleId = Number(this.route.snapshot.paramMap.get('moduleid'))

    // Get the user ID from the JWT token
    this.userId = this.jwtDecoderService.getId() ?? 'Error'; //replace this with a proper error handling later, same as trainingdetailcompnonenta


    this.loadTrainingData();
    this.loadProgressData();

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

        const currentModule = this.modules.find(m => m.id === this.moduleId);

        // Safely find the module and its chapter
        this.chapters = currentModule?.chapters || [];
        console.log(this.chapters[0].nameChapterEn)
        console.log(this.chapters[1].nameChapterEn)
        console.log('Loaded chapters:', this.chapters);
        this.chapters.forEach((e) => this.updatedChapters.push(e));

        if (this.chapters.length > 0) {
          this.activeChapterId = this.chapters[0].id; // Set to first chapter's ID
          this.cdr.detectChanges(); // Trigger change detection
        }
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
    if (this.chapters.some(ch => ch.id === chapterId)) {
      this.activeChapterId = chapterId;
    }
  }
  getChapterName(chapterId: number) {
    const chapter = this.chapters.find(ch => ch.id === chapterId);
    if (!chapter) return 'Chapter not found';

    switch (this.lang) {
      case 'en': return chapter.nameChapterEn || 'Unnamed chapter';
      case 'nl': return chapter.nameChapterNl || 'Naamloos hoofdstuk';
      case 'fr': return chapter.nameChapterFr || 'Chapitre sans nom';
      case 'de': return chapter.nameChapterDe || 'Unbenanntes Kapitel';
      default: return chapter.nameChapterEn || 'Unknown language';
    }
  }

  getActiveChapterName() {
    const chapter = this.getActiveChapter();
    if (!chapter) return 'Loading...';

    switch (this.lang) {
      case 'en': return chapter.nameChapterEn || 'Unnamed chapter';
      case 'nl': return chapter.nameChapterNl || 'Naamloos hoofdstuk';
      case 'fr': return chapter.nameChapterFr || 'Chapitre sans nom';
      case 'de': return chapter.nameChapterDe || 'Unbenanntes Kapitel';
      default: return chapter.nameChapterEn || 'Unknown language';
    }
  }

  getActiveChapterDescription() {
    const chapter = this.getActiveChapter();
    if (!chapter) return 'Loading...';

    switch (this.lang) {
      case 'en': return chapter.descriptionChapterEn || 'No description available';
      case 'nl': return chapter.descriptionChapterNl || 'Geen beschrijving beschikbaar';
      case 'fr': return chapter.descriptionChapterFr || 'Pas de description disponible';
      case 'de': return chapter.descriptionChapterDe || 'Keine Beschreibung verfügbar';
      default: return chapter.descriptionChapterEn || 'No description available';
    }
  }
  getActiveChapterContent(): string {
    const chapter = this.getActiveChapter();
    if (!chapter) return 'Loading content...';

    switch (this.lang) {
      case 'en': return this.formatText(chapter.textChapterEn);
      case 'nl': return this.formatText(chapter.textChapterNl);
      case 'fr': return this.formatText(chapter.textChapterFr);
      case 'de': return this.formatText(chapter.textChapterDe);
      default: return this.formatText(chapter.textChapterEn);
    }
  }
  getActiveChapter(): Chapter | undefined {
    return this.chapters.find(ch => ch.id === this.activeChapterId);
  }
  getCurrentChapterIndex(): number {
    const index = this.chapters.findIndex(chapter => chapter.id === this.activeChapterId);
    return index >= 0 ? index : 0; // Zorg altijd voor een geldige index
  }
  hasNextChapter(): boolean {
    const currentIndex = this.getCurrentChapterIndex();
    return currentIndex < this.chapters.length - 1 && this.chapters.length > 0;
  }


  hasPreviousChapter(): boolean {
    const currentIndex = this.getCurrentChapterIndex();
    return currentIndex > 0 && this.chapters.length > 0;
  }
  goToNextChapter(): void {
    if (this.hasNextChapter()) {
      const currentIndex = this.getCurrentChapterIndex();
      this.activeChapterId = this.chapters[currentIndex + 1].id;
    }
  }
  getVideoUrl(video: Video | undefined): string {
    if (!video) return '';
    return `${video.filePath}/${video.storedName}`;
  }

  goToPreviousChapter(): void {
    if (this.hasPreviousChapter()) {
      const currentIndex = this.getCurrentChapterIndex();
      this.activeChapterId = this.chapters[currentIndex - 1].id;
    }
  }
  getDisplayChapterNumber(): number {
    return this.getCurrentChapterIndex() + 1; // Toon chapters vanaf 1 i.p.v. 0
  }

  // Method to check if a chapter is completed
  isChapterCompleted(chapterId: number): boolean {
    if (!this.moduleProgress || !this.moduleProgress.completedChapters) {
      return false;
    }
    return this.moduleProgress.completedChapters.includes(chapterId);
  }

  // Method to mark a chapter as completed
  completeChapter(chapterId: number): void {
    if (this.isCompletingChapter || this.isChapterCompleted(chapterId)) {
      return; // Prevent multiple clicks or completing already completed chapters
    }

    this.isCompletingChapter = true;



    this.progressService.completeChapter(
      this.userId,
      this.trainingId,
      this.moduleId,
      chapterId
    ).subscribe({
      next: (updatedProgress) => {
        this.trainingProgress = updatedProgress;

        // Update the module progress
        if (this.trainingProgress && this.trainingProgress.moduleProgress) {
          this.moduleProgress = this.trainingProgress.moduleProgress.find(
            mp => mp.moduleId === this.moduleId
          );
        }

        this.isCompletingChapter = false;
      },
      error: (error) => {
        console.error('Error completing chapter', error);
        this.isCompletingChapter = false;
      }
    });
  }

  private loadProgressData(): void {
    this.progressService.getTrainingProgress(this.userId, this.trainingId).subscribe({
      next: (progressData) => {
        this.trainingProgress = progressData;

        // Update the module progress
        if (this.trainingProgress && this.trainingProgress.moduleProgress) {
          this.moduleProgress = this.trainingProgress.moduleProgress.find(
            mp => mp.moduleId === this.moduleId
          );
        }
      },
      error: (error) => {
        console.error('Error loading progress data', error);
      }
    });
  }

}
