import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { TrainingService } from '../training.service';
import { ActivatedRoute, Router } from '@angular/router';
import { training } from '../training-employee/training-employee.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-update-training',
  imports: [FormsModule],
  templateUrl: './update-training.component.html',
  styleUrl: './update-training.component.css'
})
export class UpdateTrainingComponent {
  training: training | null = null;
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private trainingService: TrainingService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const trainingId = Number(this.route.snapshot.paramMap.get('id'));
    if (trainingId) {
      this.getTrainingDetails(trainingId);
    }
  }

  //retrieve training data from database with id from the path
  getTrainingDetails(id: number): void {
    console.log(id);
    this.trainingService.getTrainingById(id).subscribe(
      (training) => {
        this.training = training;
        console.log(this.training);
      },
      (error) => {
        this.errorMessage = 'Failed to load training details.';
      }
    );
  }
  updateTraining() {
    if (this.training?.id) {
      this.trainingService.updateTraining(this.training.id, this.training).subscribe(
        (data) => {
          this.router.navigate(['/trainingEmployee']);
          alert('Training updated successfully!');
        },
        (error) => {
          this.errorMessage = 'Failed to update training.';
        }
      )
    }
  }

  confirmDelete() {
    const confirmDelete = window.confirm('Are you sure you want to delete this training?');
    if (confirmDelete && this.training?.id) {
      this.deleteTraining(this.training?.id);
    }
  }

  deleteTraining(id: number) {
    this.trainingService.deleteTraining(id).subscribe({
      next: () => {
        console.log('Training deleted successfully');
        this.router.navigate(['/trainingEmployee']);
      },
      error: (err) => {
        console.error('Error deleting training:', err);
      }
    });
  }

  deleteModule(trainingId: number, moduleId: number) {
    const confirmDelete = window.confirm('Are you sure you want to delete this module?');
    if (confirmDelete) {
      this.trainingService.deleteModule(trainingId, moduleId).subscribe({
        next: (updatedTraining) => {
          console.log('Module deleted successfully');

          // Update the training object with the updated modules from the backend
          if (this.training?.modules && updatedTraining && updatedTraining.modules) {
            this.training.modules = updatedTraining.modules;
          }
        },
        error: (err) => {
          console.error('Error deleting module:', err);
        }
      });
    }
  }

  addModule() {
    if (!this.training) return;

    if (!this.training.modules) {
      this.training.modules = [];
    }

    const highestId = Math.max(...this.training.modules.map(module => module.id));

    const newModule = {
      id: highestId + 1,
      nameModule: '',
      descriptionModule: '',
      chapters: [] // or default empty array
    };
    this.training?.modules.push(newModule);
    console.log(this.training?.modules);
  }
}
