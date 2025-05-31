import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PhotoUploadService {
  constructor(private http: HttpClient) { }

  private readonly baseUrl = `${environment.apiUrl}/trainings`;

  uploadChapterPhotos(
    trainingId: number,
    chapterIndex: number,
    files: File[]
  ): Observable<PhotoUploadResponse[]> {
    const formData = new FormData();
    files.forEach((file, index) => {
      formData.append(`photos`, file, `chapter_${chapterIndex}_photo_${index}`);
    });

    const url = `${this.baseUrl}/${trainingId}/chapters/${chapterIndex}/upload-photos`;
    return this.http.post<PhotoUploadResponse[]>(url, formData);
  }

  createFilePreview(file: File): Observable<string> {
    return new Observable(observer => {
      const reader = new FileReader();
      reader.onload = (e: any) => observer.next(e.target.result);
      reader.readAsDataURL(file);
    });
  }
}

interface PhotoUploadResponse {
  id: number;
  url: string;
}
