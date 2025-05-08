import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { Skipper } from '../../models/Skipper.model';


@Injectable({
  providedIn: 'root'
})
export class SkipperService {

  constructor(private http: HttpClient) { }

  getSkippers(): Observable<Skipper[]> {
    
    const skippersList = localStorage.getItem('skippers_List');
    if (skippersList) {

      const parsedSkippers = JSON.parse(skippersList) as Skipper[];
      return of(parsedSkippers);

    } 

    return throwError(() => new Error('No skippers found in local storage')); 
  }
}
