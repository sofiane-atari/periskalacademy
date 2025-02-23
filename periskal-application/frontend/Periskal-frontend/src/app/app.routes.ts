import { Routes } from '@angular/router';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TestComponent } from './test/test.component';

export const routes: Routes = [
    { path: 'trainingEmployee', component: TrainingEmployeeComponent },
    { path: 'test', component: TestComponent},
];
