import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { BillingService } from '../../services/Billing/billing.service';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-billing-fleetmanager',
  imports: [ReactiveFormsModule, TranslatePipe],
  templateUrl: './billing-fleetmanager.component.html',
  styleUrl: './billing-fleetmanager.component.css'
})
export class FleetManagerBillingComponent implements OnInit {
  @Input() userIdsToBill: string[] = [];
  @Input() examIdToBill!: number;
  @Input() examAttemptsToBill!: number;

  @Output() billingSuccess = new EventEmitter<any>();
  @Output() billingCancel = new EventEmitter<void>();

  billingForm!: FormGroup;
  fleetManagerId!: string | null;

  constructor(
    private fb: FormBuilder,
    private billingSvc: BillingService, 
    private snackBar: MatSnackBar,
    private jwtDecoderService: JwtDecoderService
  ) {}

  ngOnInit() {
    this.fleetManagerId = this.jwtDecoderService.getId();

    // Get fleetmanager info and use it later to pre-fill the form
    this.billingForm = this.fb.group({
      fullName: ['', Validators.required],
      companyName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tax: [''],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required],
      countryCode: ['', Validators.required],

    });
  }

  onSubmit() {
    if (this.billingForm.valid) {
      const payerBillingDetails = this.billingForm.value; // Fleet Manager's details

      // Construct the list of Billing objects for the batch endpoint
      const billingBatchPayload = this.userIdsToBill.map(userId => {
        return {
          // Payer's (Fleet Manager's) details
          fullName: payerBillingDetails.fullName,
          companyName: payerBillingDetails.companyName,
          email: payerBillingDetails.email,
          tax: payerBillingDetails.tax,
          address: payerBillingDetails.address,
          city: payerBillingDetails.city,
          state: payerBillingDetails.state,
          zipCode: payerBillingDetails.zipCode,
          countryCode: payerBillingDetails.countryCode,

          // Exam and specific user details
          examId: this.examIdToBill,
          userId: userId, 
          examAttempts: this.examAttemptsToBill,
          

        };
      });

      
      this.billingSvc.postBillingBatch(billingBatchPayload).subscribe({
        next: (response) => {
          const snackBarConfig: MatSnackBarConfig = {
            duration: 3000,
            verticalPosition: 'bottom',
            horizontalPosition: 'center',
            panelClass: ['snackbar-success'],
          };
          this.snackBar.open('Billing processed successfully for selected users!', 'Close', snackBarConfig);
          this.billingSuccess.emit(response); 
        },
        error: (err) => {
          let errorMessage = 'Billing failed. Unknown error.';
          if (err.error && err.error.message) {
            errorMessage = `Billing failed: ${err.error.message}`;
          } 
          const snackBarConfig: MatSnackBarConfig = {
            duration: 7000, 
            verticalPosition: 'bottom',
            horizontalPosition: 'center',
            panelClass: ['snackbar-error'],
          };
          this.snackBar.open(errorMessage, 'Close', snackBarConfig);

        }
      });
    } else {
      this.billingForm.markAllAsTouched();
      this.snackBar.open('Please fill all required billing fields.', 'Close', { duration: 3000, verticalPosition: 'bottom', horizontalPosition: 'center' });
    }
  }

  goBack() {
    this.billingCancel.emit();
  }
}

