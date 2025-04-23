import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Skipper {
  id: string;
  username: string;
  firstname: string;
  lastname: string;
  shipname: string;
  role: string;
  products: string[];
}

@Component({
  selector: 'app-skippers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './skippers.component.html',
  styleUrls: ['./skippers.component.css']
})
export class SkippersComponent implements OnInit {
  skippers: Skipper[] = [];
  selectedSkipperIndex: number | null = null;

  ngOnInit() {
    this.fetchSkippersData();
  }

  fetchSkippersData() {

    this.skippers = [
      {
        id: "PER00181",
        username: "Skipper_1",
        firstname: "Firstname",
        lastname: "Lastname",
        shipname: "Night of the Seas",
        role: "Skipper",
        products: ["Trackpilot", "AIS", "ECDIS"]
      },
      {
        id: "PER00182",
        username: "Skipper_2",
        firstname: "John",
        lastname: "Doe",
        shipname: "Northern Star",
        role: "Skipper",
        products: ["Trackpilot", "ECDIS"]
      },
      {
        id: "PER00183",
        username: "Skipper_3",
        firstname: "Jane",
        lastname: "Smith",
        shipname: "Ocean Explorer",
        role: "Skipper",
        products: ["AIS", "ECDIS"]
      }
    ];
  }

  toggleSkipperDetails(index: number) {
    if (this.selectedSkipperIndex === index) {
      this.selectedSkipperIndex = null; 
    } else {
      this.selectedSkipperIndex = index; 
    }
  }
}
