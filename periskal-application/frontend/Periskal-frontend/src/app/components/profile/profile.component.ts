import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { AuthenticationService } from '../../services/authentication.service';
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

  constructor(
    private jwtDecoderService: JwtDecoderService,
    private authService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit() {
    
    const username = this.jwtDecoderService.getUsername();
    const role = this.jwtDecoderService.getUserRole();
    const id = this.jwtDecoderService.getId();
    const firstname = this.jwtDecoderService.getFirstname();
    const lastname = this.jwtDecoderService.getLastname();
    const shipname = this.jwtDecoderService.getShipname();
    const products = this.jwtDecoderService.getProducts();


      this.userProfile = {
      username: username ?? 'Not available',
      role: role ?? 'Not available',
      id: id ?? 'Not available',
      firstname: firstname ?? 'Not available',
      lastname: lastname ?? 'Not available',
      shipname: shipname ?? 'Not available',
      products: products ?? []
      };
 
  }

  logout() { 
    this.authService.logout();
    this.router.navigate(['/login']); // there is probably a better way to do this but this works for now
  }
}
