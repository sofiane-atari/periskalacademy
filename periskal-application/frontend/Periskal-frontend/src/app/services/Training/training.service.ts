import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Training } from '../../models/training.model';
import { Photo } from '../../models/photo.model';
import { Video } from '../../models/video.model';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  constructor(private client: HttpClient) { }

  getTrainingData(): Observable<Training[]> {
    return this.client.get<Training[]>("http://backend.localhost:8080/api/trainings");
  }

  getTrainingById(trainingId: number): Observable<Training> {
    return this.client.get<Training>(`http://backend.localhost:8080/api/trainings/${trainingId}`);
  }

  postTraining(trainingData: any): Observable<any> {
    return this.client.post("http://localhost:8080/api/trainings", trainingData);
  }

  updateTraining(trainingId: number, trainingData: any): Observable<any> {
    return this.client.put(`http://localhost:8080/api/trainings/${trainingId}`, trainingData);
  }

  deleteTraining(trainingId: number): Observable<any> {
    return this.client.delete(`http://localhost:8080/api/trainings/${trainingId}`)
  }

  deleteModule(trainingId: number, moduleId: number): Observable<any> {
    return this.client.delete(`http://localhost:8080/api/trainings/${trainingId}/modules/${moduleId}`)
  }
  updateModules(trainingId: number, modules: any): Observable<any> {
    return this.client.put(`http://localhost:8080/api/trainings/${trainingId}/modules`, modules)
  }
  addModuleToTraining(trainingId: number, module: any): Observable<any> {
    return this.client.post(`http://localhost:8080/api/trainings/${trainingId}/modules`, module);
  }

  getFilteredTrainingData(role: string, products?: string[]): Observable<Training[]> {
    let params = new HttpParams().set('role', role);

    if (products && products.length > 0) {
      products.forEach(product => {
        params = params.append('products', product);
      });
    }

    return this.client.get<Training[]>("http://backend.localhost:8080/api/trainings/filtered", { params });
  }

  deletePhoto(photoId: number) {
    return this.client.delete(`http://localhost:8080/api/photos/${photoId}`);
  }

  addPhotos(chapterId: number, photos?: Photo[]) {
    return this.client.post(`http://localhost:8080/api/photos/${chapterId}`, photos)
  }

  uploadVideo(videoFile: File): Observable<Video> {
    const formData = new FormData();
    // formData.append('video', videoFile, videoFile.name);
    // return this.client.post<string>(`http://localhost:8080/api/trainings/video`, formData)
    formData.append('video', videoFile);
    return this.client.post<Video>('http://localhost:8080/api/trainings/video', formData);
  }

  loadVideo(videoRemoteFilePath: string): Observable<Blob> {
    return this.client.get(`http://localhost:8080/api/trainings/video/stream?videoPath=${encodeURIComponent(videoRemoteFilePath)}`, { responseType: 'blob' });
  }
}
