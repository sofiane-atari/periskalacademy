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
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { interval } from 'rxjs/internal/observable/interval';
import { Subscription } from 'rxjs';

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
  updatedChapters: Chapter[] = []; // Still used, but activeChapterId will be an ID

  mimeType: string = 'video/mp4';
  videoUrl: string | null = null;
  errorMessage = "";
  loading: boolean = false;

  trainingId: number = 0;
  moduleId: number = 0;
  activeChapterId: number = -1; // Now stores the ID of the active chapter

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
    private progressService: ProgressService,
    private snackBar: MatSnackBar) {
  }


  formatText(content: string | undefined): string {
    if (!content) return 'No content available';
    return content.replace(/\n/g, '<br>');
  }


  ngOnInit(): void {
    this.lang = localStorage.getItem('lang');
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    this.moduleId = Number(this.route.snapshot.paramMap.get('moduleid'))
    this.userId = this.jwtDecoderService.getId() ?? 'Error';

    this.loadTrainingData();
    this.loadProgressData();
  }

  getChapterArray(chapters: any): any[] {
    if (Array.isArray(chapters)) return chapters;
    if (chapters instanceof Object) return Object.values(chapters);
    return [];
  }

  private loadTrainingData(): void {
    this.trainingSvc.getTrainingById(this.trainingId).subscribe({
      next: (data) => {
        this.training = data;
        this.modules = this.training.modules || [];
        const currentModule = this.modules.find(m => m.id === this.moduleId);
        this.chapters = currentModule?.chapters || [];
        this.updatedChapters = [...this.chapters];

        if (this.chapters.length > 0) {
          this.activeChapterId = this.chapters[0].id; // Initialize activeChapterId with the ID of the first chapter
          this.cdr.detectChanges();
        }
        this.loadVideo();
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

  getActiveChapter(): Chapter | undefined {
    return this.chapters.find(ch => ch.id === this.activeChapterId);
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

  getCurrentChapterIndex(): number {
    const index = this.chapters.findIndex(chapter => chapter.id === this.activeChapterId);
    return index >= 0 ? index : 0;
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
      if (!this.isChapterCompleted(this.activeChapterId))
        this.completeChapter(this.activeChapterId);
      this.activeChapterId = this.chapters[currentIndex + 1].id;
    } else {
      if (!this.isChapterCompleted(this.activeChapterId))
        this.completeChapter(this.activeChapterId);
      const snackBarConfig: MatSnackBarConfig = {
        duration: 3000,
        verticalPosition: 'bottom',
        horizontalPosition: 'left',
        panelClass: ['snackbar-success'],
        politeness: 'assertive'
      };

      this.snackBar.open('Training completed!', 'Close', snackBarConfig);
      this.router.navigate(['/training', this.trainingId]);
    }
  }

  goToPreviousChapter(): void {
    if (this.hasPreviousChapter()) {
      const currentIndex = this.getCurrentChapterIndex();
      this.activeChapterId = this.chapters[currentIndex - 1].id;
    }
  }

  getDisplayChapterNumber(): number {
    return this.getCurrentChapterIndex() + 1;
  }

  isChapterCompleted(chapterIndex: number): boolean {
    if (!this.moduleProgress || !this.moduleProgress.completedChapters) {
      return false;
    }
    const chapterIdToCheck = this.chapters[chapterIndex]?.id;
    return chapterIdToCheck ? this.moduleProgress.completedChapters.includes(chapterIdToCheck) : false;
  }

  completeChapter(chapterId: number): void {
    if (this.isCompletingChapter || this.isChapterCompleted(this.getCurrentChapterIndex())) {
      return;
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


  loadVideo(): void {
    const currentChapter = this.getActiveChapter();
    if (currentChapter?.videoRemoteFilePath) {
      this.loading = true;
      this.trainingSvc.loadVideo(currentChapter.videoRemoteFilePath).subscribe({
        next: (videoBlob: Blob) => {
          this.mimeType = videoBlob.type || 'video/mp4';
          this.videoUrl = URL.createObjectURL(videoBlob);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading video:', error);
          this.loading = false;
          this.errorMessage = 'Failed to load video.';
          this.videoUrl = null;
        },
      });
    } else {
      this.videoUrl = null;
      this.errorMessage = 'No video path available for this chapter.';
    }
  }
}