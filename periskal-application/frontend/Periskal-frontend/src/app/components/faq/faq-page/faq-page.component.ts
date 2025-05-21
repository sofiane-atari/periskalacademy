import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { FaqService } from '../../../services/Faq/faq.service';
import { HttpClientModule } from '@angular/common/http';
import { Faq } from '../../../models/Faq.model';

@Component({
  selector: 'app-faq-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './faq-page.component.html',
  styleUrl: './faq-page.component.scss'
})
export class FaqPageComponent {
  faqs: any[] = [];
  faqForm = new FormGroup({
    question: new FormControl('', Validators.required),
    answer: new FormControl('', Validators.required)
  });

  constructor(private faqService: FaqService) {
    this.loadFaqs();
  }

  loadFaqs() {
    this.faqService.getFaqs().subscribe(data => this.faqs = data);
  }

  addFaq() {
  if (this.faqForm.invalid) {
    return;
  }

  const formValue = this.faqForm.value;

  const newFaq: Faq = {
    id: 0,
    question: formValue.question ?? '',
    answer: formValue.answer ?? ''
  };

  this.faqService.createFaq(newFaq).subscribe(() => {
    this.loadFaqs();
    this.faqForm.reset();
  });
}

  deleteFaq(id: number) {
    this.faqService.deleteFaq(id).subscribe(() => this.loadFaqs());
  }
}
