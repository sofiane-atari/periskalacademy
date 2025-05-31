import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BillingService {
  constructor(private client: HttpClient) { }

  private baseUrl = environment.apiUrl;

  postBilling(billingData: any): Observable<any> {
    return this.client.post(`${this.baseUrl}/billing`, billingData);
  }

  postBillingBatch(billingData: any): Observable<any> {
    return this.client.post(`${this.baseUrl}/billing/batch`, billingData);
  }

}
