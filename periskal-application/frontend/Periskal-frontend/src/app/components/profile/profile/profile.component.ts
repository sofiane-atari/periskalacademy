import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { AuthenticateResponse } from '../../../models/authenticate-response.model';

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

    if (username && role) {
      this.userProfile = {
      username: username,
      role: role,
      id: 'Will be available',
      firstname: 'Will be available',
      lastname: 'Will be available'
      };
    } else {
      // Handle the case where username or role is null
      console.error('Username or role is null');
    }
  }
}
