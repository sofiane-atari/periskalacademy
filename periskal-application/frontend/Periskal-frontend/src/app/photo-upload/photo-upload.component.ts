import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PhotoUploadService } from '../photo-upload.service';

@Component({
  selector: 'app-photo-upload',
  imports: [],
  templateUrl: './photo-upload.component.html',
  styleUrls: ['./photo-upload.component.css']
})
export class PhotoUploadComponent {
  @Input() chapterIndex: number = 0;
  @Output() photosUpdated = new EventEmitter<File[]>();

  photoPreviewUrls: string[] = [];
  selectedPhotos: File[] = [];

  constructor(private photoUploadService: PhotoUploadService) { }

  onFileSelected(event: any) {
    const files: FileList = event.target.files;

    Array.from(files).forEach(file => {
      this.photoUploadService.createFilePreview(file)
        .subscribe(preview => {
          this.photoPreviewUrls.push(preview);
          this.selectedPhotos.push(file);
          this.photosUpdated.emit(this.selectedPhotos);
        });
    });
  }

  removePhoto(index: number) {
    console.log(this.selectedPhotos[index]);
    console.log(this.photoPreviewUrls[index]);
    this.photoPreviewUrls.splice(index, 1);
    this.selectedPhotos.splice(index, 1);
    this.photosUpdated.emit(this.selectedPhotos);
  }
}