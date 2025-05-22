import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscription, takeWhile, timer } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExamStateService {
  private currentExamId: number | null = null;
  private totalExamDurationSeconds: number | null = null;
  private startTime: number | null = null;
  private timerSubscription: Subscription | null = null;

  private _remainingTime = new BehaviorSubject<number>(0);
  public readonly remainingTime$: Observable<number> = this._remainingTime.asObservable();

  private _examFinished = new BehaviorSubject<boolean>(false);
  public readonly examFinished$: Observable<boolean> = this._examFinished.asObservable();

  constructor() {
    this.restoreStateFromSessionStorage();
  }

  startOrResumeExam(examId: number, durationSeconds: number): void {
    if (this.currentExamId === examId && this.timerSubscription && !this._examFinished.value) {
      console.log('Exam timer already in progress for this exam, resuming.');
      return;
    }

    this.resetTimerState(false);

    this.currentExamId = examId;
    this.totalExamDurationSeconds = durationSeconds;

    if (this.startTime === null) {
      this.startTime = Date.now();
    }

    this.startTimer();
    this.saveStateToSessionStorage();
    this._examFinished.next(false);
  }

  private startTimer(): void {
    if (this.totalExamDurationSeconds === null || this.startTime === null) {
      console.warn('Cannot start timer: Total duration or start time missing.');
      return;
    }

    this.stopTimer();

    const totalDuration = this.totalExamDurationSeconds;
    const elapsed = Math.floor((Date.now() - this.startTime) / 1000);
    let initialRemaining = totalDuration - elapsed;

    if (initialRemaining <= 0) {
      this._remainingTime.next(0);
      this.finishExam();
      return;
    }

    this._remainingTime.next(initialRemaining);

    this.timerSubscription = timer(1000, 1000)
      .pipe(
        takeWhile(() => this._remainingTime.value > 0)
      )
      .subscribe(() => {
        const newRemaining = this._remainingTime.value - 1;
        this._remainingTime.next(newRemaining);
        if (newRemaining <= 0) {
          this.finishExam();
        }
      });
  }

  stopTimer(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
      this.timerSubscription = null;
    }
  }

  finishExam(): void {
    if (this._examFinished.value) {
      return;
    }
    this.stopTimer();
    this._examFinished.next(true);
    console.log('Exam finished or manually submitted!');
    this.clearStateFromSessionStorage();
  }

  getCurrentExamId(): number | null {
    return this.currentExamId;
  }

  private saveStateToSessionStorage(): void {
    const state = {
      currentExamId: this.currentExamId,
      startTime: this.startTime,
      totalExamDurationSeconds: this.totalExamDurationSeconds,
    };
    sessionStorage.setItem('activeExamTimerState', JSON.stringify(state));
  }

  private restoreStateFromSessionStorage(): void {
    const storedState = sessionStorage.getItem('activeExamTimerState');
    if (storedState) {
      try {
        const state = JSON.parse(storedState);
        if (state.currentExamId && state.startTime && state.totalExamDurationSeconds) {
          const elapsed = Math.floor((Date.now() - state.startTime) / 1000);
          if (state.totalExamDurationSeconds - elapsed <= 0) {
            console.log('Restored exam state indicates time has already expired. Finishing exam.');
            this.clearStateFromSessionStorage();
            this._remainingTime.next(0);
            this._examFinished.next(true);
          } else {
            this.currentExamId = state.currentExamId;
            this.startTime = state.startTime;
            this.totalExamDurationSeconds = state.totalExamDurationSeconds;
            this._examFinished.next(false);
            this.startTimer();
            console.log('Exam timer state restored and timer restarted.');
          }
        }
      } catch (e) {
        console.error('Failed to parse exam timer state from session storage:', e);
        this.clearStateFromSessionStorage();
      }
    }
  }

  private clearStateFromSessionStorage(): void {
    sessionStorage.removeItem('activeExamTimerState');
  }

  resetTimerState(clearStorage: boolean = true): void {
    this.stopTimer();
    this.currentExamId = null;
    this.totalExamDurationSeconds = null;
    this.startTime = null;
    this._remainingTime.next(0);
    this._examFinished.next(false);
    if (clearStorage) {
      this.clearStateFromSessionStorage();
    }
  }
}