import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Training } from '../../models/training.model';
import { Photo } from '../../models/photo.model';
import { Video } from '../../models/video.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  constructor(private client: HttpClient) { }

  private readonly baseUrl = `${environment.apiUrl}/trainings`;
  private readonly photoUrl = `${environment.apiUrl}/photos`;

  getTrainingData(): Observable<Training[]> {
    return this.client.get<Training[]>(this.baseUrl);
  }

  getTrainingById(trainingId: number): Observable<Training> {
    return this.client.get<Training>(`${this.baseUrl}/${trainingId}`);
  }

  postTraining(trainingData: any): Observable<any> {
    return this.client.post(this.baseUrl, trainingData);
  }

  updateTraining(trainingId: number, trainingData: any): Observable<any> {
    return this.client.put(`${this.baseUrl}/${trainingId}`, trainingData);
  }

  deleteTraining(trainingId: number): Observable<any> {
    return this.client.delete(`${this.baseUrl}/${trainingId}`);
  }

  deleteModule(trainingId: number, moduleId: number): Observable<any> {
    return this.client.delete(`${this.baseUrl}/${trainingId}/modules/${moduleId}`);
  }
  updateModules(trainingId: number, modules: any): Observable<any> {
    return this.client.put(`${this.baseUrl}/${trainingId}/modules`, modules);
  }
  addModuleToTraining(trainingId: number, module: any): Observable<any> {
    return this.client.post(`${this.baseUrl}/${trainingId}/modules`, module);
  }

  getFilteredTrainingData(role: string, products?: string[]): Observable<Training[]> {
    let params = new HttpParams().set('role', role);

    if (products && products.length > 0) {
      products.forEach(product => {
        params = params.append('products', product);
      });
    }

    return this.client.get<Training[]>(`${this.baseUrl}/filtered`, { params });
  }

  deletePhoto(photoId: number) {
    return this.client.delete(`${this.photoUrl}/${photoId}`);
  }

  addPhotos(chapterId: number, photos?: Photo[]) {
    return this.client.post(`${this.photoUrl}/${chapterId}`, photos);
  }

  uploadVideo(videoFile: File): Observable<Video> {
    const formData = new FormData();
    formData.append('video', videoFile);
    return this.client.post<Video>(`${this.baseUrl}/video`, formData);
  }

  loadVideo(videoRemoteFilePath: string): Observable<Blob> {
    return this.client.get(`${this.baseUrl}/video/stream?videoPath=${encodeURIComponent(videoRemoteFilePath)}`, { responseType: 'blob' });
  }
}
