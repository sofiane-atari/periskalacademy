import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';
import { UpdateTrainingComponent } from './update-training/update-training.component';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { ModuleDetailComponent } from './module-detail/module-detail.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [

    // Public Routes
    {
        path: '',
        children: [
            { path: 'login', component: LoginComponent },
            { path: 'decode-token', component: DongleLoginComponent },
        ]
    },

    // Protected Routes
    {
        path: '',
        canActivate: [authGuard],
        children: [
            { path: 'trainingEmployee', component: TrainingEmployeeComponent },
            { path: 'add', component: AddTrainingComponent },
            { path: 'training/:id', component: TrainingDetailComponent },
            { path: 'edit/:id', component: UpdateTrainingComponent },
            { path: 'training/:id/module/:moduleid', component: ModuleDetailComponent },
            { path: 'profile', component: ProfileComponent }
        ]
    },

    // Fallback Route
    { path: '**', redirectTo: 'login' }
];

