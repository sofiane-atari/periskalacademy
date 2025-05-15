import { Component, ElementRef, EventEmitter, Output, ViewChild } from '@angular/core';

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.css']
})
export class VideoUploadComponent {
  @ViewChild('videoPreview') videoPreview!: ElementRef<HTMLVideoElement>;
  @Output() videoSelected = new EventEmitter<File>();

  selectedFile: File | null = null;
  isDragOver = false;

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) this.handleFile(file);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    const file = event.dataTransfer?.files[0];
    if (file) this.handleFile(file);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
  }

  handleFile(file: File): void {
    if (!file.type.startsWith('video/')) {
      alert('Alleen video bestanden zijn toegestaan');
      return;
    }

    if (file.size > 100 * 1024 * 1024) {
      alert('Bestand is te groot (max 100MB)');
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = (e) => {
      this.videoPreview.nativeElement.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);

    this.videoSelected.emit(file);
  }

  clearFile(): void {
    this.selectedFile = null;
    this.isDragOver = false;
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' bytes';
    else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    else return (bytes / 1048576).toFixed(1) + ' MB';
  }
}