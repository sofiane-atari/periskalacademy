import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { AuthenticateResponse } from '../../models/authenticate-response.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  userProfile: Partial<AuthenticateResponse> = {};

  constructor(private jwtDecoderService: JwtDecoderService) {}

  ngOnInit() {
    
    const username = this.jwtDecoderService.getUsername();
    const role = this.jwtDecoderService.getUserRole();
    const id = this.jwtDecoderService.getId();
    const firstname = this.jwtDecoderService.getFirstname();
    const lastname = this.jwtDecoderService.getLastname();


      this.userProfile = {
      username: username ?? 'Not available',
      role: role ?? 'Not available',
      id: id ?? 'Not available',
      firstname: firstname ?? 'Not available',
      lastname: lastname ?? 'Not available'
      };
 
  }
}
