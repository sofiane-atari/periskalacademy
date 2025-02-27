import { Component } from '@angular/core';
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
    private router: Router
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
}
