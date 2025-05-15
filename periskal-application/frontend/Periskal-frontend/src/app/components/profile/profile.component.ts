import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { AuthenticationService } from '../../services/Authentication/authentication.service';
import { AuthenticateResponse } from '../../models/authenticate-response.model';
import { TranslatePipe } from '@ngx-translate/core';
import { finalize } from 'rxjs';
import { CertificateService } from '../../services/Certificates/certificate.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  userProfile: Partial<AuthenticateResponse> = {};
  activeTab: 'info' | 'products' | 'settings' = 'info';
  isDownloading = false;

  constructor(
    private jwtDecoderService: JwtDecoderService,
    private authService: AuthenticationService,
    private router: Router,
    private certificateService: CertificateService
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

  setActiveTab(tab: 'info' | 'products' | 'settings') {
    this.activeTab = tab;
  }

}
