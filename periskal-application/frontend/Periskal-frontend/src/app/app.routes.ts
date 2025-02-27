import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DongleLoginComponent } from './components/dongle-login/dongle-login.component';
import { TrainingEmployeeComponentComponent } from './components/training-employee-component/training-employee-component.component';

export const routes: Routes = [
  { path: 'decode-token', component: DongleLoginComponent },
  { path: 'trainingEmployee', component: TrainingEmployeeComponentComponent },
  { path: '', redirectTo: '/decode-token', pathMatch: 'full' } // Default naar login
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
