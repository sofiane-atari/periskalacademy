import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TestComponent } from './test/test.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';
import { UpdateTrainingComponent } from './update-training/update-training.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },  
    { path: 'trainingEmployee', component: TrainingEmployeeComponent },
    { path: 'test', component: TestComponent},
    { path: 'training/:id', component: TrainingDetailComponent }, 
    { path: 'add', component: AddTrainingComponent},
    { path: 'edit/:id', component: UpdateTrainingComponent},
    { path: '**', redirectTo: 'login' }        // Redirect all unknown routes to login, Has to be at the end of the list!!!
];

