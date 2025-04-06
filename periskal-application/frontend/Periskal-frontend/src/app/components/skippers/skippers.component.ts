import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface CrewMember {
  name: string;
  shipName: string;
  attributes: {
    [key: string]: string;
  };
}

@Component({
  selector: 'app-skippers',
  standalone: true,
  imports: [CommonModule],
  template: `
    <table id="crewTable">
      <thead>
        <tr>
          <th>Name</th>
          <th>Ship Name</th>
          <th *ngFor="let attr of allAttributesArray">{{attr}}</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let crew of crewMembers; let i = index">
          <td>{{crew.name}}</td>
          <td>{{crew.shipName}}</td>
          <td *ngFor="let attr of allAttributesArray">
            {{crew.attributes[attr] || '-'}}
          </td>
          <td class="actions-cell">
            <button class="view-btn" (click)="viewCrewMember(i)">View</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
  styleUrl: './skippers.component.css'
})
export class SkippersComponent implements OnInit {
  crewMembers: CrewMember[] = [];
  allAttributes: Set<string> = new Set();
  allAttributesArray: string[] = [];

  ngOnInit() {
    //this.fetchCrewData(); call to service layer
  }

  async fetchCrewData() {
    try {
      // Replace with api endpoint calling in service layer
      // const response = await fetch('your-api-endpoint');
      // const data = await response.json();
      // this.crewMembers = data;

      // Sample data
      this.crewMembers = [
        {
          name: "John Smith",
          shipName: "HMS Victory",
          attributes: {
            rank: "Captain",
            experience: "15 years",
            specialty: "Navigation"
          }
        },
        {
          name: "Jane Doe",
          shipName: "SS Adventure",
          attributes: {
            rank: "First Mate",
            experience: "8 years",
            specialty: "Engineering"
          }
        }
      ];

      // Extract all unique attributes from crew members.
      this.crewMembers.forEach(crew => {
        Object.keys(crew.attributes).forEach(attr => {
          this.allAttributes.add(attr);
        });
      });
      
      this.allAttributesArray = Array.from(this.allAttributes);
    } catch (error) {
      console.error("Error fetching crew data:", error);
    }
  }

  viewCrewMember(index: number) {
    const crew = this.crewMembers[index];
    alert(`Viewing details for: ${crew.name} (${crew.shipName})`);
    // Replace with better way to show details alert is fine for debuging for now
  }
}