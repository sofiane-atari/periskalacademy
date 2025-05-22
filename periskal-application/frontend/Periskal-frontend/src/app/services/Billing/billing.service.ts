import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BillingService {
  constructor(private client: HttpClient) { }

  postBilling(billingData: any): Observable<any> {
    return this.client.post("http://localhost:8080/api/billing", billingData);
  }

  postBillingBatch(billingData: any): Observable<any> {
    return this.client.post("http://localhost:8080/api/billing/batch", billingData);
  }

}
