import { Component } from '@angular/core';
import { TrainingService } from '../../../services/Training/training.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Training } from '../../../models/training.model';
import { ConfirmPromptComponent } from '../../confirm-prompt/confirm-prompt.component';
import { MatDialog } from '@angular/material/dialog'

@Component({
  selector: 'app-update-training',
  imports: [FormsModule, TranslatePipe, ConfirmPromptComponent, TranslatePipe],
  templateUrl: './update-training.component.html',
  styleUrl: './update-training.component.css'
})
export class UpdateTrainingComponent {
  training: Training | null = null;
  loading: boolean = true;
  errorMessage: string = '';
  trainingId: number = 0;

  constructor(
    private trainingService: TrainingService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.trainingId) {
      this.getTrainingDetails(this.trainingId);
    }
  }

  //retrieve training data from database with id from the path
  getTrainingDetails(id: number): void {
    this.trainingService.getTrainingById(id).subscribe({
      next: (training) => {
        this.training = training;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load training details.';
      }
    });
  }


  updateTraining() {
    if (this.training?.id) {
      this.trainingService.updateTraining(this.training.id, this.training).subscribe({
        next: (data) => {
          alert('Training updated successfully!');
          this.router.navigate(['/trainingEmployee']);
        },
        error: (error) => {
          this.errorMessage = 'Failed to update training.';
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
        cancelText: "Cancel"
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.training?.id) {
        this.trainingService.deleteTraining(this.training?.id).subscribe({
          next: () => {
            this.router.navigate(['/trainingEmployee']);
          },
          error: (err) => {
            console.error('Error deleting training:', err);
          }
        });
      }
    });
  }

  addModule() {
    if (!this.training) return;

    if (!this.training.modules) {
      this.training.modules = [];
    }

    const highestId = Math.max(...this.training.modules.map(module => module.id));

    const newModule = {
      id: highestId + 1,
      nameModule_en: '',
      descriptionModule_en: '',
      nameModule_nl: '',
      descriptionModule_nl: '',
      nameModule_fr: '',
      descriptionModule_fr: '',
      nameModule_de: '',
      descriptionModule_de: '',
      chapters: []
    };
    this.training?.modules.push(newModule);
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
}
