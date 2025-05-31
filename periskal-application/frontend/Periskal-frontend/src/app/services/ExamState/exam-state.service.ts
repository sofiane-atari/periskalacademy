import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscription, takeWhile, timer } from 'rxjs';
import { Question } from '../../models/question.model';

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

  private _examState = new BehaviorSubject<{
    questions: Question[] | null;
    selectedAnswers: (number | undefined)[];
  }>({
    questions: null,
    selectedAnswers: []
  });
  public readonly examState$: Observable<{
    questions: Question[] | null;
    selectedAnswers: (number | undefined)[];
  }> = this._examState.asObservable();

  constructor() {
    this.restoreStateFromSessionStorage();
  }

  getCurrentExamState(): { questions: Question[] | null; selectedAnswers: (number | undefined)[] } {
    return this._examState.value;
  }

  // New method to get the current exam finished state
  isExamFinished(): boolean {
    return this._examFinished.value;
  }

  startOrResumeExam(examId: number, durationSeconds: number, questions?: Question[]): void {
    if (this.currentExamId === examId && this.timerSubscription && !this._examFinished.value) {
      return;
    }

    this.resetTimerState(false);

    this.currentExamId = examId;
    this.totalExamDurationSeconds = durationSeconds;

    if (this.startTime === null) {
      this.startTime = Date.now();
    }

    const savedState = this.restoreStateFromSessionStorage();
    if (savedState && savedState.questions && savedState.currentExamId === examId) {
      this._examState.next({
        questions: savedState.questions,
        selectedAnswers: savedState.selectedAnswers || []
      });
    } else if (questions) {
      this._examState.next({
        questions,
        selectedAnswers: new Array(questions.length).fill(undefined)
      });
    }

    this.startTimer();
    this.saveStateToSessionStorage();
    this._examFinished.next(false);
  }

  updateSelectedAnswers(selectedAnswers: (number | undefined)[]): void {
    const currentState = this._examState.value;
    this._examState.next({ ...currentState, selectedAnswers });
    this.saveStateToSessionStorage();
  }

  private startTimer(): void {
    if (this.totalExamDurationSeconds === null || this.startTime === null) {
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
      questions: this._examState.value.questions?.map(q => ({
        id: q.id,
        textEn: q.textEn,
        textNl: q.textNl,
        textFr: q.textFr,
        textDe: q.textDe,
        photos: q.photos ? q.photos.map(p => ({ imageData: p.imageData })) : undefined,
        answers: q.answers ? q.answers.map(a => ({ 
        id: a.id,
        textEn: a.textEn,
        textNl: a.textNl,
        textFr: a.textFr,
        textDe: a.textDe, })) : undefined,
      })) || null,
      selectedAnswers: this._examState.value.selectedAnswers.map(answer => answer === undefined ? null : answer)
    };
    try {
      sessionStorage.setItem('activeExamTimerState', JSON.stringify(state));
    } catch (e) {
    }
  }

  private restoreStateFromSessionStorage(): any {
    const storedState = sessionStorage.getItem('activeExamTimerState');
    if (storedState) {
      try {
        const state = JSON.parse(storedState);
        if (state.currentExamId && state.startTime && state.totalExamDurationSeconds) {
          const elapsed = Math.floor((Date.now() - state.startTime) / 1000);
          if (state.totalExamDurationSeconds - elapsed <= 0) {
            this.clearStateFromSessionStorage();
            this._remainingTime.next(0);
            this._examFinished.next(true);
            return null;
          } else {
            this.currentExamId = state.currentExamId;
            this.startTime = state.startTime;
            this.totalExamDurationSeconds = state.totalExamDurationSeconds;
            const selectedAnswers = state.selectedAnswers?.map((answer: number | null) => answer === null ? undefined : answer) || [];
            this._examState.next({
              questions: state.questions || null,
              selectedAnswers
            });
            this._examFinished.next(false);
            this.startTimer();
            return state;
          }
        }
      } catch (e) {
        this.clearStateFromSessionStorage();
      }
    }
    return null;
  }

  private clearStateFromSessionStorage(): void {
    sessionStorage.removeItem('activeExamTimerState');
    this._examState.next({ questions: null, selectedAnswers: [] });
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