import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './training-employee/training-employee.component';
import { TrainingDetailComponent } from './training-detail/training-detail.component';
import { AddTrainingComponent } from './add-training/add-training.component';
import { UpdateTrainingComponent } from './update-training/update-training.component';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { ModuleDetailComponent } from './module-detail/module-detail.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';
import { UpdateModuleComponent } from './update-module/update-module.component';
import { AddModuleComponent } from './add-module/add-module.component';

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
            { path: 'edit/:id/modules', component: UpdateModuleComponent },
            { path: 'add/:id/module', component: AddModuleComponent },
            { path: 'training/:id/module/:moduleid', component: ModuleDetailComponent },
            { path: 'profile', component: ProfileComponent }
        ]
    },

    // Fallback Route
    { path: '**', redirectTo: 'login' }
];

RouterModule.forRoot(routes, {
    enableTracing: true // ✅ This will log route changes in the console
  })
  
