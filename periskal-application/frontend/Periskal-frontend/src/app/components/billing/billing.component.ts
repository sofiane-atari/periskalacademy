import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BillingService } from '../../services/Billing/billing.service';

@Component({
  selector: 'app-billing',
  imports: [ReactiveFormsModule],
  templateUrl: './billing.component.html',
  styleUrl: './billing.component.css'
})
export class BillingComponent implements OnInit {
  billingForm!: FormGroup;

  constructor(private fb: FormBuilder, private billingSvc: BillingService) { }

  ngOnInit() {
    this.billingForm = this.fb.group({
      fullName: ['', Validators.required],
      companyName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tax: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required],
      country: ['', Validators.required],
      trainingId: [''],
    });
  }

  onSubmit() {
    if (this.billingForm.valid) {
      const billingFormData = this.billingForm.value
      this.billingSvc.postBilling(billingFormData).subscribe({
        next: (response) => {
          console.log('Success:', response);
          // Show success message, reset form, etc.
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
