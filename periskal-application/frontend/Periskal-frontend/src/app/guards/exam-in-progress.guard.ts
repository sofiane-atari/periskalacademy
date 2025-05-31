import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExamStateService } from '../services/ExamState/exam-state.service';

export interface CanComponentDeactivate {
  canDeactivate: () => boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ExamInProgressGuard implements CanDeactivate<CanComponentDeactivate> {
  constructor(
    private examStateService: ExamStateService,
    private snackBar: MatSnackBar
  ) { }

  canDeactivate(): boolean {
    const isFinished = this.examStateService.isExamFinished();

    if (!isFinished) {
      this.snackBar.open(
        'You cannot leave the exam until it is finished.',
        'OK',
        {
          duration: 4000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['snackbar-error']
        }
      );
    }

    return isFinished;
  }
}
