import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class SkipperService {

  constructor(private http: HttpClient) { }

  getSkippers() {
    
    const skippersList = localStorage.getItem('skippers_List');
    if (skippersList) {
      return JSON.parse(skippersList); // Return the skippers list from local storage as an observable
    } 

    return ; // Return an empty array if no skippers are found in local storage
  
  }
}
