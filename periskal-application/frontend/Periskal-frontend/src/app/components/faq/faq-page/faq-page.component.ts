import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { FaqService } from '../../../services/Faq/faq.service';
import { HttpClientModule } from '@angular/common/http';
import { Faq } from '../../../models/Faq.model';
import { RoleAuthorizationService } from '../../../services/RoleAuthorization/role-authorization.service';
import { trigger, transition, style, animate } from '@angular/animations';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-faq-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, TranslatePipe],
  templateUrl: './faq-page.component.html',
  styleUrl: './faq-page.component.css',
  animations: [
    trigger('expandCollapse', [
      transition(':enter', [
        style({ height: '0', opacity: 0, overflow: 'hidden' }),
        animate('300ms ease-out', style({ height: '*', opacity: 1 }))
      ]),
      transition(':leave', [
        style({ height: '*', opacity: 1, overflow: 'hidden' }),
        animate('300ms ease-in', style({ height: '0', opacity: 0 }))
      ])
    ]),
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('200ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class FaqPageComponent implements OnInit {
  faqs: Faq[] = [];
  faqForm: FormGroup;
  expandedFaqId: number | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  showDeleteConfirm: { [key: string]: boolean } = {};
  isSubmitting = false;
  showAddForm = false;

  constructor(
    private fb: FormBuilder,
    public roleAuth: RoleAuthorizationService,
    private faqService: FaqService,
    private translate: TranslateService
  ) {
    this.faqForm = this.fb.group({
      question: ['', [Validators.required, Validators.minLength(5)]],
      answer: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    this.loadFaqs();
  }

  loadFaqs(): void {
    this.faqService.getFaqs().subscribe({
      next: (faqs) => {
        this.faqs = faqs;
      },
      error: (error) => {
        this.showError('Er is een fout opgetreden bij het laden van de FAQ-items.');
        console.error('Error loading FAQs:', error);
      }
    });
  }

  addFaq(): void {
    if (this.faqForm.invalid) return;
    
    this.isSubmitting = true;
    this.clearMessages();
    
    const newFaq: Faq = this.faqForm.value;
    
    this.faqService.createFaq(newFaq).subscribe({
      next: (addedFaq) => {
        this.faqs = [...this.faqs, addedFaq];
        this.faqForm.reset();
        this.showSuccess(this.translate.instant('faq.createSuccess'));
        this.isSubmitting = false;
      },
      error: (error) => {
        this.showError(this.translate.instant('faq.createError'));
        console.error('Error adding FAQ:', error);
        this.isSubmitting = false;
      }
    });
  }

  deleteFaq(id: number): void {
    if (this.showDeleteConfirm[id]) {
      this.faqService.deleteFaq(id).subscribe({
        next: () => {
          this.faqs = this.faqs.filter(faq => faq.id !== id);
          this.showSuccess(this.translate.instant('faq.deleteSuccess'));
          this.showDeleteConfirm[id] = false;
        },
        error: (error) => {
          this.showError(this.translate.instant('faq.deleteError'));
          console.error('Error deleting FAQ:', error);
          this.showDeleteConfirm[id] = false;
        }
      });
    } else {
      // First click shows confirmation
      this.showDeleteConfirm = { [id]: true };
      
      // Auto-hide after 3 seconds
      setTimeout(() => {
        this.showDeleteConfirm[id] = false;
      }, 3000);
    }
  }

  toggleFaq(id: number): void {
    this.expandedFaqId = this.expandedFaqId === id ? null : id;
  }

  cancelDelete(id: number, event: Event): void {
    event.stopPropagation();
    this.showDeleteConfirm[id] = false;
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = null;
    setTimeout(() => this.errorMessage = null, 5000);
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = null;
    setTimeout(() => this.successMessage = null, 5000);
  }

  private clearMessages(): void {
    this.errorMessage = null;
    this.successMessage = null;
  }
}
