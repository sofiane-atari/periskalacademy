import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { TrainingEmployeeComponent } from './components/Training/training-employee/training-employee.component';
import { TrainingDetailComponent } from './components/Training/training-detail/training-detail.component';
import { AddTrainingComponent } from './components/Training/add-training/add-training.component';
import { UpdateTrainingComponent } from './components/Training/update-training/update-training.component';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { ModuleDetailComponent } from './components/Training/module-detail/module-detail.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';
import { AddModuleComponent } from './components/Training/add-module/add-module.component';
import { SkippersComponent } from './components/skippers/skippers.component';
import { AddExamComponent } from './components/exam/add-exam/add-exam.component';
import { TakeExamComponent } from './components/exam/take-exam/take-exam.component';
import { UpdateExamComponent } from './components/exam/update-exam/update-exam.component';
import { AddChapterComponent } from './components/Training/add-chapter/add-chapter.component';

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
            { path: 'add-training', component: AddTrainingComponent },
            { path: 'add-exam', component: AddExamComponent },
            { path: 'update-exam/:id', component: UpdateExamComponent },
            { path: 'take-exam/:id', component: TakeExamComponent },
            { path: 'training/:id', component: TrainingDetailComponent },
            { path: 'edit/:id', component: UpdateTrainingComponent },
            { path: 'add/:id/module', component: AddModuleComponent },
            { path: 'add/:id/chapter', component: AddChapterComponent },
            { path: 'training/:id/module/:moduleid', component: ModuleDetailComponent },
            { path: 'profile', component: ProfileComponent },
            { path: 'skippers', component: SkippersComponent }
        ]
    },

    // Fallback Route
    { path: '**', redirectTo: 'login' }
];

RouterModule.forRoot(routes, {
    enableTracing: true // ✅ This will log route changes in the console
})

