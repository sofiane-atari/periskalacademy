import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModuleService {

  constructor(private client: HttpClient) { }

  AddChapterToModule(moduleId: number, chapter: any): Observable<any> {
    return this.client.post(`http://localhost:8080/api/modules/${moduleId}/chapters`, chapter);
  }

}
