import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PhotoUploadService {
  constructor(private http: HttpClient) { }

  uploadChapterPhotos(
    trainingId: number,
    chapterIndex: number,
    files: File[]
  ): Observable<PhotoUploadResponse[]> {
    const formData = new FormData();
    files.forEach((file, index) => {
      formData.append(`photos`, file, `chapter_${chapterIndex}_photo_${index}`);
    });

    return this.http.post<PhotoUploadResponse[]>(
      `/api/trainings/${trainingId}/chapters/${chapterIndex}/upload-photos`,
      formData
    );
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
