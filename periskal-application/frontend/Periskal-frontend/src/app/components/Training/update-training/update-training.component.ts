import { Component } from '@angular/core';
import { TrainingService } from '../../../services/Training/training.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { ConfirmPromptComponent } from '../../confirm-prompt/confirm-prompt.component';
import { MatDialog } from '@angular/material/dialog'
import { MatSnackBar, MatSnackBarConfig, MatSnackBarModule } from '@angular/material/snack-bar'
import { ModuleService } from '../../../services/Module/module.service';
import { Photo } from '../../../models/photo.model';
import { PhotoUploadComponent } from "../../photo-upload/photo-upload.component";

@Component({
  selector: 'app-update-training',
  imports: [FormsModule, TranslatePipe, TranslatePipe, MatSnackBarModule, PhotoUploadComponent],
  templateUrl: './update-training.component.html',
  styleUrl: './update-training.component.css'
})
export class UpdateTrainingComponent {
  training: Training | null = null;
  loading: boolean = true;
  errorMessage: string = '';
  trainingId: number = 0;
  photos: Photo[] | null = null;
  newPhotos: { [key: string]: Photo[] } = {};

  constructor(
    private trainingService: TrainingService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.trainingId) {
      this.getTrainingDetails(this.trainingId);
    }
  }
  getTrainingDetails(id: number): void {
  this.trainingService.getTrainingById(id).subscribe({
    next: (training) => {
      this.training = training;

      // Ensure all modules have a chapters array and initialize newPhotos
      if (this.training?.modules) {
        this.training.modules.forEach((module, moduleIndex) => {
          if (!module.chapters) {
            module.chapters = [];
          }
          // Initialize newPhotos for each chapter in this module
          module.chapters.forEach((chapter, chapterIndex) => {
            const key = `${moduleIndex}_${chapterIndex}`;
            if (!this.newPhotos[key]) {
              this.newPhotos[key] = [];
            }
          });
        });
      }

      this.loading = false;
    },
    error: (error) => {
      this.errorMessage = 'Failed to load training details.';
    }
  });
}

  updateTraining() {
    if (this.training?.id) {
      // Remove photos from the payload before sending
      const trainingToUpdate = structuredClone(this.training) as any;

      trainingToUpdate.modules?.forEach((module: any) => {
        module.chapters?.forEach((chapter: any) => {
          delete chapter.photosEn;
        });
      });

      this.trainingService.updateTraining(this.training.id, this.training).subscribe({
        next: (data) => {
          const snackBarConfig: MatSnackBarConfig = {
            duration: 3000,
            verticalPosition: 'bottom',
            horizontalPosition: 'left',
            panelClass: ['snackbar-success'],
            politeness: 'assertive'
          };

          this.snackBar.open('Training updated successfully!', 'Close', snackBarConfig);
          this.router.navigate(['/trainingEmployee']);
        },
        error: (error) => {
          this.errorMessage = 'Failed to update training.';
          const errorSnackBarConfig: MatSnackBarConfig = {
            duration: 5000,
            verticalPosition: 'bottom',
            horizontalPosition: 'left',
            panelClass: ['snackbar-error'],
            politeness: 'assertive'
          };

          this.snackBar.open('Error updating training.', 'Close', errorSnackBarConfig);
        }
      });
    }
  }

  deleteTraining() {
  const dialogRef = this.dialog.open(ConfirmPromptComponent, {
    width: '600px',
    data: {
      title: 'Delete Training',
      message: 'Are you sure you want to delete this training? This action cannot be undone.',
      confirmText: 'Delete',
      cancelText: 'Cancel'
    }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result && this.training?.id) {
      this.trainingService.deleteTraining(this.training.id).subscribe({
        next: () => {
          const snackBarConfig: MatSnackBarConfig = {
            duration: 3000,
            verticalPosition: 'bottom',
            horizontalPosition: 'left',
            panelClass: ['snackbar-success'],
            politeness: 'assertive'
          };

          this.snackBar.open('Training has been successfully deleted.', 'Close', snackBarConfig);
          this.router.navigate(['/trainingEmployee']);
        },
        error: (err) => {
          console.error('Error deleting training:', err);
          const errorSnackBarConfig: MatSnackBarConfig = {
            duration: 5000,
            verticalPosition: 'bottom',
            horizontalPosition: 'left',
            panelClass: ['snackbar-error'],
            politeness: 'assertive'
          };

          this.snackBar.open('Error deleting training.', 'Close', errorSnackBarConfig);
        }
        });
      }
    });
  }

  savePhotos(moduleIndex: number, chapterIndex: number): void {
    const key = moduleIndex + '_' + chapterIndex;
    const photos = this.newPhotos[key] || [];

    if (!photos.length) return;
    const chapterId = this.training?.modules[moduleIndex].chapters[chapterIndex].id;
    if (chapterId != null) {
      this.trainingService.addPhotos(chapterId, photos).subscribe({
        next: () => {
          this.getTrainingDetails(this.trainingId);
        },
        error: (err) => {
          console.error("error by adding photos", err);
        }
      });
    }
    this.newPhotos[key] = [];
  }

  addModule() {
    if (!this.training) return;

    if (!this.training.modules) {
      this.training.modules = [];
    }

    // Calculate the highest module ID or use 0 if no modules exist
    const highestId = this.training.modules.length > 0
      ? Math.max(...this.training.modules.map(module => module.id || 0))
      : 0;

    const newModule = {
      id: highestId + 1,
      nameModuleEn: '',
      descriptionModuleEn: '',
      nameModuleNl: '',
      descriptionModuleNl: '',
      nameModuleFr: '',
      descriptionModuleFr: '',
      nameModuleDe: '',
      descriptionModuleDe: '',
      chapters: []
    };
    this.training?.modules.push(newModule);
  }

  removePhoto(moduleIndex: number, chapterIndex: number, photoIndex: number) {
    const photoId = this.training?.modules[moduleIndex].chapters[chapterIndex].photos[photoIndex].id;
    const dialogRef = this.dialog.open(ConfirmPromptComponent, {
      width: '600px',
      data: {
        title: 'Delete Module',
        message: 'Are you sure you want to delete this module? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: "Cancel"
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (photoId != null)
          this.trainingService.deletePhoto(photoId).subscribe({
            next: () => {
              const snackBarConfig: MatSnackBarConfig = {
                duration: 3000,
                verticalPosition: 'bottom',
                horizontalPosition: 'left',
                panelClass: ['snackbar-success'],
                politeness: 'assertive'
              };
              this.getTrainingDetails(this.trainingId);

              this.snackBar.open('Photo has successfully been deleted.', 'Close', snackBarConfig);
            },
            error: (err) => {
              const errorSnackBarConfig: MatSnackBarConfig = {
                duration: 5000,
                verticalPosition: 'bottom',
                horizontalPosition: 'left',
                panelClass: ['snackbar-error'],
                politeness: 'assertive'
              };

              this.snackBar.open('Error deleting photo.', 'Close', errorSnackBarConfig);
            }
          });
      }
    });
  }

  goToModuleAddPage() {
    this.router.navigate(['add', this.trainingId, 'module'])
  }

  removeModule(index: number) {
    const dialogRef = this.dialog.open(ConfirmPromptComponent, {
      width: '600px',
      data: {
        title: 'Delete Module',
        message: 'Are you sure you want to delete this module? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: "Cancel"
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.training?.modules.splice(index, 1);
      }
    });
  }

  goToChapterAddPage(moduleIndex: number) {
    const moduleId = this.training?.modules[moduleIndex].id;
    this.router.navigate(['add', this.trainingId, 'module', moduleId, 'chapter'])
  }

  removeChapter(moduleIndex: number, chapterIndex: number) {
    const dialogRef = this.dialog.open(ConfirmPromptComponent, {
      width: '600px',
      data: {
        title: 'Delete Chapter',
        message: 'Are you sure you want to delete this chapter? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: "Cancel"
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.training?.modules[moduleIndex].chapters.splice(chapterIndex, 1);
      }
    });
  }
}