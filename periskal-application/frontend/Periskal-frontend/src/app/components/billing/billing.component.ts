import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BillingService } from '../../services/Billing/billing.service';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-billing',
  imports: [ReactiveFormsModule],
  templateUrl: './billing.component.html',
  styleUrl: './billing.component.css'
})
export class BillingComponent implements OnInit {
  billingForm!: FormGroup;
  examId!: number;

  constructor(private fb: FormBuilder, private billingSvc: BillingService, private snackBar: MatSnackBar, private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {
    const rawExamId = this.route.snapshot.paramMap.get('examid');
    this.examId = rawExamId ? Number(rawExamId) : NaN;

    this.billingForm = this.fb.group({
      fullName: ['', Validators.required],
      companyName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tax: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required],
      countryCode: ['', Validators.required],
      examId: [this.examId, Validators.required],
    });
  }


  onSubmit() {
    this.billingForm.patchValue({ examId: this.examId });
    console.log(this.billingForm);
    const billingFormData = this.billingForm.value;
    console.log(this.billingForm.valid);
    console.log(billingFormData);
    if (this.billingForm.valid) {
      const billingFormData = this.billingForm.value;
      console.log(billingFormData);
      this.billingSvc.postBilling(billingFormData).subscribe({
        next: (response) => {
          console.log('Success:', response);
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
          console.error('Error:', err);
          // Show error feedback
        }
      });
    } else {
      this.billingForm.markAllAsTouched();
    }
  }
}
