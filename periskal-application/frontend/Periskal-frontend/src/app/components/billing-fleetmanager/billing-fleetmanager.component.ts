import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { BillingService } from '../../services/Billing/billing.service';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { RoleAuthorizationService } from '../../services/RoleAuthorization/role-authorization.service';
import { TranslatePipe } from '@ngx-translate/core';
import { MatStepperModule } from '@angular/material/stepper';

@Component({
  selector: 'app-billing-fleetmanager',
  imports: [ReactiveFormsModule, TranslatePipe, MatStepperModule],
  templateUrl: './billing-fleetmanager.component.html',
  styleUrls: ['./billing-fleetmanager.component.css']
})
export class FleetManagerBillingComponent implements OnInit {
  @Input() userIdsToBill: string[] = [];
  @Input() examId!: number;
  @Input() examAttemptsToBill!: number;
  @Input() examPrice!: number;

  @Output() billingSuccess = new EventEmitter<any>();
  @Output() billingCancel = new EventEmitter<void>();

  billingForm!: FormGroup;
  fleetManagerId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private billingSvc: BillingService,
    private snackBar: MatSnackBar,
    private jwtDecoderService: JwtDecoderService,
    private roleAuth: RoleAuthorizationService
  ) {}

  ngOnInit() {
    this.fleetManagerId = this.jwtDecoderService.getId();

    console.log('Billing component inputs:', {
      userIdsToBill: this.userIdsToBill,
      examId: this.examId,
      examAttemptsToBill: this.examAttemptsToBill,
      examPrice: this.examPrice,
      isAdmin: this.roleAuth.isAdmin(),
      isSupport: this.roleAuth.isSupport()
    });

    // Bypass billing for admin/support users
    if (this.roleAuth.isAdmin() || this.roleAuth.isSupport()) {
      this.billingSuccess.emit({ bypassed: true }); // Emit success without billing
      return;
    }

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
      examId: ['']
    });
  }

  onSubmit() {
    if (this.billingForm.valid) {
      const payerBillingDetails = this.billingForm.value;

      const billingBatchPayload = this.userIdsToBill.map(userId => ({
        fullName: payerBillingDetails.fullName,
        companyName: payerBillingDetails.companyName,
        email: payerBillingDetails.email,
        tax: payerBillingDetails.tax,
        address: payerBillingDetails.address,
        city: payerBillingDetails.city,
        state: payerBillingDetails.state,
        zipCode: payerBillingDetails.zipCode,
        countryCode: payerBillingDetails.countryCode,
        examId: this.examId,
        userId: userId,
        examAttempts: this.examAttemptsToBill
      }));

      this.billingSvc.postBillingBatch(billingBatchPayload).subscribe({
        next: (response) => {
          const snackBarConfig: MatSnackBarConfig = {
            duration: 3000,
            verticalPosition: 'bottom',
            horizontalPosition: 'center',
            panelClass: ['snackbar-success']
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
            panelClass: ['snackbar-error']
          };
          this.snackBar.open(errorMessage, 'Close', snackBarConfig);
        }
      });
    } else {
      this.billingForm.markAllAsTouched();
      this.snackBar.open('Please fill all required billing fields.', 'Close', {
        duration: 3000,
        verticalPosition: 'bottom',
        horizontalPosition: 'center'
      });
    }
  }

  goBack() {
    this.billingCancel.emit();
  }
}