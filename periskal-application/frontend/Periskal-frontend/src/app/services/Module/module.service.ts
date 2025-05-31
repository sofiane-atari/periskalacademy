import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ModuleService {

  constructor(private client: HttpClient) { }

  private readonly baseUrl = `${environment.apiUrl}/modules`;

  AddChapterToModule(moduleId: number, chapterDTO: any): Observable<any> {
    return this.client.post(`${this.baseUrl}/${moduleId}/chapters`, chapterDTO);
  }

}
