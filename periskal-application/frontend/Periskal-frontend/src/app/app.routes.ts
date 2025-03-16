import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';
import { UpdateTrainingComponent } from './update-training/update-training.component';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';


export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'decode-token', component: DongleLoginComponent },
    { 
        path: 'trainingEmployee', 
        component: TrainingEmployeeComponent,
        canActivate: [authGuard]
    },
    { 
        path: 'training/:id', 
        component: TrainingDetailComponent,
        canActivate: [authGuard]
    },
    { 
        path: 'add', 
        component: AddTrainingComponent,
        canActivate: [authGuard]
    },
    { 
        path: 'edit/:id', 
        component: UpdateTrainingComponent,
        canActivate: [authGuard]
    },
    { 
        path: 'profile', 
        component: ProfileComponent,
        canActivate: [authGuard]
    },
    { path: '**', redirectTo: 'login' } // Redirect all unknown routes to login, Has to be at the end of the list!!!
];

