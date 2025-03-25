import { Component } from '@angular/core';
import { TrainingService } from '../training.service';
import { ActivatedRoute, Router } from '@angular/router';
import { module, training } from '../training-employee/training-employee.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-update-module',
  imports: [FormsModule],
  templateUrl: './update-module.component.html',
  styleUrl: './update-module.component.css'
})
export class UpdateModuleComponent {
  training: training | null = null;
  modules: module[] = [];
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
    //console.log(id);
    this.trainingService.getTrainingById(id).subscribe(
      (training) => {
        this.training = training;
        console.log(this.training);
        if (this.training)
          this.modules = this.training.modules;
      },
      (error) => {
        this.errorMessage = 'Failed to load training details.';
      }
    );
  }

  deleteModule(trainingId: number, moduleId: number) {
    const confirmDelete = window.confirm('Are you sure you want to delete this module?');
    if (confirmDelete) {
      this.trainingService.deleteModule(trainingId, moduleId).subscribe({
        next: (updatedTraining) => {
          console.log('Module deleted successfully');

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
  updateModules() {
    //todo: implement post into database
    //console.log(this.modules);
    if (this.training?.id) {
      this.trainingService.updateModules(this.training.id, this.modules).subscribe(
        (data) => {
          alert('Training updated successfully!');
        },
        (error) => {
          this.errorMessage = 'Failed to update training.';
        }
      )
    }
  }
}
