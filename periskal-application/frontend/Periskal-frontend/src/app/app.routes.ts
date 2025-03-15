import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';
import { UpdateTrainingComponent } from './update-training/update-training.component';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { ProfileComponent } from './components/profile/profile/profile.component';


export const routes: Routes = [
    { path: 'login', component: LoginComponent },  
    { path: 'trainingEmployee', component: TrainingEmployeeComponent },
    { path: 'training/:id', component: TrainingDetailComponent }, 
    { path: 'add', component: AddTrainingComponent},
    { path: 'edit/:id', component: UpdateTrainingComponent},
    { path: 'decode-token', component: DongleLoginComponent },
    { path: 'profile', component: ProfileComponent },
    { path: '**', redirectTo: 'login' }        // Redirect all unknown routes to login, Has to be at the end of the list!!!
];


