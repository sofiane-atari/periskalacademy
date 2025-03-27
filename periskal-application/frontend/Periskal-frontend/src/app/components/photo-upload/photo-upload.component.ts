import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { PhotoUploadService } from '../../services/Photoupload/photo-upload.service';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-photo-upload',
  imports: [TranslatePipe],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PhotoUploadComponent),
      multi: true
    }
  ],
  templateUrl: './photo-upload.component.html',
  styleUrls: ['./photo-upload.component.css']
})
export class PhotoUploadComponent implements ControlValueAccessor {
  photos: PhotoUpload[] = [];

  onChange: any = () => { };
  onTouched: any = () => { };

  writeValue(value: any): void {
    if (value) {
      this.photos = value;
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  onFileSelected(event: any) {
    const files: FileList = event.target.files;

    Array.from(files).forEach(file => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const newPhoto: PhotoUpload = {
          file: file,
          imageData: e.target.result
        };
        this.photos.push(newPhoto);
        this.onChange(this.photos);
        this.onTouched();
      };
      reader.readAsDataURL(file);
    });
  }

  removePhoto(index: number) {
    this.photos.splice(index, 1);
    this.onChange(this.photos);
    this.onTouched();
  }
}

interface PhotoUpload {
  file: File;
  imageData: string;
}