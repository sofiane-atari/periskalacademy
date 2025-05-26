import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { RoleAuthorizationService } from '../../services/RoleAuthorization/role-authorization.service';
import { ContactService } from '../../services/Contact/contact.service';
import { MatSnackBar, MatSnackBarConfig, MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-contact-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule, TranslatePipe],
  templateUrl: './contact-page.component.html',
  styleUrl: './contact-page.component.css',
})

export class ContactPageComponent {
  contactForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    public roleAuth: RoleAuthorizationService,
    private contactService: ContactService,
    private snackBar: MatSnackBar,
    private translateService: TranslateService
  ) {
    this.contactForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      message: ['', [Validators.required, Validators.minLength(10)]]
    });
  }


  onSubmit(): void {
    if (this.contactForm.invalid) return;
    
    this.isSubmitting = true;
    
    const email = this.contactForm.get('email')?.value;
    const message = this.contactForm.get('message')?.value;
    
    this.contactService.sendContactForm(email, message).subscribe({
      next: () => {
        this.contactForm.reset();
        this.showSuccess();
        this.isSubmitting = false;
      },
      error: (error) => {
        this.showError();
        console.error('Error sending contact form:', error);
        this.isSubmitting = false;
      }
    });
  }

  private showError(): void {
    const snackBarConfig: MatSnackBarConfig = {
      duration: 3000,
      verticalPosition: 'bottom',
      horizontalPosition: 'left',
      panelClass: ['snackbar-error'],
      politeness: 'assertive'
    };

    this.snackBar.open(
      this.translateService.instant('CONTACT_FORM_ERROR'), 
      this.translateService.instant('CLOSE'), 
      snackBarConfig
    );
  }

  private showSuccess(): void {
    const snackBarConfig: MatSnackBarConfig = {
      duration: 3000,
      verticalPosition: 'bottom',
      horizontalPosition: 'left',
      panelClass: ['snackbar-success'],
      politeness: 'assertive'
    };

    this.snackBar.open(
      this.translateService.instant('CONTACT_FORM_SUCCESS'), 
      this.translateService.instant('CLOSE'), 
      snackBarConfig
    );
  }

}
