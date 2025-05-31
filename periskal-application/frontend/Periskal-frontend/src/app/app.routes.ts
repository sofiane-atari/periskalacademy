import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/Training/dashboard/dashboard-employee.component';
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
import { CertificatesComponent } from './components/certificates/certificates.component';
import { ExamsListComponent } from './components/exams-list/exams-list.component';
import { BillingComponent } from './components/billing/billing.component';
import { FaqPageComponent } from './components/faq/faq-page/faq-page.component';
import { ContactPageComponent } from './components/contact/contact-page.component';
import { ExamInProgressGuard } from './guards/exam-in-progress.guard';

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
            { path: 'dashboard', component: DashboardComponent },
            { path: 'add-training', component: AddTrainingComponent },
            { path: 'add-exam', component: AddExamComponent },
            { path: 'update-exam/:id', component: UpdateExamComponent },
            {
                path: 'take-exam/:id',
                component: TakeExamComponent,
                canDeactivate: [ExamInProgressGuard]
            },
            { path: 'training/:id', component: TrainingDetailComponent },
            { path: 'edit/:id', component: UpdateTrainingComponent },
            { path: 'add/:id/module', component: AddModuleComponent },
            { path: 'add/:trainingid/module/:moduleid/chapter', component: AddChapterComponent },
            { path: 'training/:id/module/:moduleid', component: ModuleDetailComponent },
            { path: 'profile', component: ProfileComponent },
            { path: 'skippers', component: SkippersComponent },
            { path: 'certificates', component: CertificatesComponent },
            { path: 'exams', component: ExamsListComponent },
            { path: 'billing/:examid', component: BillingComponent },
            { path: 'faq-page', component: FaqPageComponent },
            { path: 'contact', component: ContactPageComponent }
        ]
    },

    // Fallback Route
    { path: '**', redirectTo: 'login' }
];



