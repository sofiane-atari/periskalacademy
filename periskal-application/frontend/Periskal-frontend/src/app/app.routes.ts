import { Routes } from '@angular/router';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TestComponent } from './test/test.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';

export const routes: Routes = [
    { path: 'trainingEmployee', component: TrainingEmployeeComponent },
    { path: 'test', component: TestComponent},
    { path: 'training/:id', component: TrainingDetailComponent }, 
    { path: 'submit', component: AddTrainingComponent},
];
