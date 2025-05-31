import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BillingService } from '../../services/Billing/billing.service';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { ExamService } from '../../services/Exam/exam.service';
import { TranslatePipe } from '@ngx-translate/core';
import { MatStepperModule } from '@angular/material/stepper';
import { RoleAuthorizationService } from '../../services/RoleAuthorization/role-authorization.service';

@Component({
  selector: 'app-order',
  imports: [ReactiveFormsModule, TranslatePipe, MatStepperModule],
  templateUrl: './billing.component.html',
  styleUrl: './billing.component.css'
})
export class BillingComponent implements OnInit {
  billingForm!: FormGroup;
  examId!: number;
  userId!: string | null;
  examAttempts!: number;
  examPrice!: number;

  constructor(private fb: FormBuilder, private billingSvc: BillingService, private snackBar: MatSnackBar, private router: Router, private route: ActivatedRoute, private jwtDecoderService: JwtDecoderService, private examService: ExamService, public roleAuth: RoleAuthorizationService) { }

  ngOnInit() {
    const rawExamId = this.route.snapshot.paramMap.get('examid');
    this.examId = rawExamId ? Number(rawExamId) : NaN;

    this.userId = this.jwtDecoderService.getId();
    this.examService.getExamById(this.examId).subscribe({
      next: (data) => {
        this.examPrice = data.price
        this.examAttempts = data.examAttempts;
      }
    })

    this.billingForm = this.fb.group({
      fullName: ['', Validators.required],
      companyName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tax: [''],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required],
      countryCode: [''],
      examId: [this.examId, Validators.required],
      userId: [this.userId, Validators.required],
      examAttempts: [this.examAttempts, Validators.required]
    });
  }


  onSubmit() {
    this.billingForm.patchValue({ userId: this.userId });
    this.billingForm.patchValue({ examId: this.examId });
    this.billingForm.patchValue({ examAttempts: this.examAttempts });
    if (this.billingForm.valid) {
      const billingFormData = this.billingForm.value;
      this.billingSvc.postBilling(billingFormData).subscribe({
        next: (response) => {
          const snackBarConfig: MatSnackBarConfig = {
            duration: 3000,
            verticalPosition: 'bottom',
            horizontalPosition: 'left',
            panelClass: ['snackbar-success'],
            politeness: 'assertive'
          };

          this.snackBar.open('Order processed successfully!', 'Close', snackBarConfig);
          this.router.navigate(['/take-exam', this.examId]);
        },
        error: (err) => {
          // Show error feedback
        }
      });
    } else {
      this.billingForm.markAllAsTouched();
    }
  }

  goBack() {
    this.router.navigate(['/dashboard'])
  }
}
