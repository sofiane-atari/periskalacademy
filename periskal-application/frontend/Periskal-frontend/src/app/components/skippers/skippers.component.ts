import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {SkipperService } from '../../services/Skippers/skipper.service';
import { Skipper } from '../../models/Skipper.model';

@Component({
  selector: 'app-skippers',
  standalone: true,
  imports: [CommonModule],
  providers: [],
  templateUrl: './skippers.component.html',
  styleUrls: ['./skippers.component.css']
})
export class SkippersComponent implements OnInit {
  skippers: Skipper[] = [];
  selectedSkipperIndex: number | null = null;
  error: string | null = null;

  constructor(private skipperService: SkipperService) {}

  ngOnInit() {
    this.fetchSkippersData();
  }

  fetchSkippersData() {
    this.skipperService.getSkippers().subscribe({
      next: (data: Skipper[]) => {
      this.skippers = data;
      },
      error: (err: unknown) => {
      console.error('Error fetching skippers:', err);
      this.error = 'Failed to load skippers. Please try again later.';   
      }
    });
  }

  toggleSkipperDetails(index: number) {
    if (this.selectedSkipperIndex === index) {
      this.selectedSkipperIndex = null; 
    } else {
      this.selectedSkipperIndex = index; 
    }
  }
}
