import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';
import { Question } from '../../../models/question.model';
import { TranslatePipe } from '@ngx-translate/core';
import { CompletedExam } from '../../../models/completed-exam.model';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { CompletedExamService } from '../../../services/CompletedExam/completed-exam.service';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ExamStateService } from '../../../services/ExamState/exam-state.service';
import { MatDialog } from '@angular/material/dialog';
import { CertificateDialogComponent } from '../certificatedialog/certificatedialog.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-take-exam',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './take-exam.component.html',
  styleUrls: ['./take-exam.component.css']
})
export class TakeExamComponent implements OnInit, OnDestroy {
  exam: Exam | null = null;
  currentQuestionIndex = 0;
  loading = true;
  error: string | null = null;
  selectedAnswers: (number | undefined)[] = [];
  remainingTime: number = 0;
  private submissionInitiatedByTimer: boolean = false;
  private isSubmitting: boolean = false;
  private timerSubscriptions: Subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService,
    private router: Router,
    private jwtDecoderService: JwtDecoderService,
    private completedExamService: CompletedExamService,
    private examStateService: ExamStateService,
    private dialog: MatDialog
  ) {}

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler(event: BeforeUnloadEvent): void {
    if (!this.examStateService.isExamFinished()) {
      event.preventDefault();
      event.returnValue = '';
    }
  }

  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.timerSubscriptions.add(
      this.examStateService.examState$.subscribe(state => {
        if (state.questions && this.examStateService.getCurrentExamId() === examId) {
          this.exam = {
            id: examId,
            questions: state.questions,
            examTitle: this.exam?.examTitle || '',
            examDescription: this.exam?.examDescription || '',
            duration: this.exam?.duration || 0,
            totalQuestions: state.questions.length,
            passingScore: this.exam?.passingScore || 100,
            trainingId: this.exam?.trainingId || 0
          } as Exam;
          this.selectedAnswers = state.selectedAnswers;
          this.loading = false;
        }
      })
    );

    this.timerSubscriptions.add(
      this.examStateService.remainingTime$.subscribe(time => {
        this.remainingTime = time;
      })
    );

    this.timerSubscriptions.add(
      this.examStateService.examFinished$.pipe(
        filter(finished => finished)
      ).subscribe(() => {
        if (!this.isSubmitting) {
          this.submissionInitiatedByTimer = true;
          this.submitExam();
        }
      })
    );

    this.loadExam(examId);
  }

  ngOnDestroy(): void {
    this.timerSubscriptions.unsubscribe();
  }

  loadExam(examId: number): void {
    this.loading = true;
    this.error = null;

    const currentExamId = this.examStateService.getCurrentExamId();
    const savedState = this.examStateService.getCurrentExamState();

    if (currentExamId === examId && savedState.questions && savedState.questions.length > 0) {
      this.exam = {
        id: examId,
        questions: savedState.questions,
        examTitle: '',
        examDescription: '',
        duration: 0,
        totalQuestions: savedState.questions.length,
        passingScore: 100,
        trainingId: 0
      } as Exam;
      // Ensure selectedAnswers matches questions length
      this.selectedAnswers = savedState.selectedAnswers?.slice(0, savedState.questions.length) || [];
      while (this.selectedAnswers.length < savedState.questions.length) {
        this.selectedAnswers.push(undefined);
      }
      this.loading = false;
    } else {
      this.examService.getExamById(examId).subscribe({
        next: (exam) => {
          exam.questions = exam.questions || [];
          const shuffledQuestions = exam.questions.sort(() => 0.5 - Math.random());
          const selectedQuestions = shuffledQuestions.slice(0, exam.totalQuestions);
          exam.questions = selectedQuestions;

          this.exam = exam;
          this.selectedAnswers = new Array(exam.questions.length).fill(undefined);
          this.loading = false;

          const examDuration = exam.duration * 60;
          if (exam.id) {
            this.examStateService.startOrResumeExam(exam.id, examDuration, exam.questions);
          }
        },
        error: (err) => {
          this.error = 'Failed to load exam questions';
          this.loading = false;
          this.examStateService.resetTimerState(true);
        }
      });
    }
  }

  get currentQuestion(): Question | null {
    if (!this.exam || !this.exam.questions || !this.exam.questions.length) return null;
    return this.exam.questions[this.currentQuestionIndex];
  }

  clearSelection(): void {
    this.selectedAnswers[this.currentQuestionIndex] = undefined;
    this.examStateService.updateSelectedAnswers(this.selectedAnswers);
  }

  nextQuestion(): void {
    if (this.exam && this.exam.questions && this.currentQuestionIndex < this.exam.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  goToQuestion(index: number): void {
    if (this.exam && this.exam.questions && index >= 0 && index < this.exam.questions.length) {
      this.currentQuestionIndex = index;
    }
  }

  selectAnswer(questionIndex: number, answerIndex: number): void {
    this.selectedAnswers[questionIndex] = answerIndex;
    this.examStateService.updateSelectedAnswers(this.selectedAnswers);
  }

  isQuestionAnswered(index: number): boolean {
    return this.selectedAnswers[index] !== undefined && this.selectedAnswers[index] !== null;
  }

 submitExam(): void {
  if (!this.exam || this.isSubmitting) {
    return;
  }

  this.isSubmitting = true;

  if (this.exam.trainingId === undefined || this.exam.trainingId === null) {
    alert('Error: Cannot submit exam. Training ID is missing from exam data. Please contact support.');
    this.isSubmitting = false;
    return;
  }

  let correctCount = 0;
  if (this.exam.questions) {
    this.exam.questions.forEach((question, index) => {
      const selected = this.selectedAnswers[index];
      if (selected !== undefined && selected + 1 === question.correctAnswer) {
        correctCount++;
      }
    });
  }

  const totalQuestions = this.exam.questions ? this.exam.questions.length : 0;
  const scorePercentage = totalQuestions > 0 ? Math.round((correctCount / totalQuestions) * 100) : 0;
  const userId = this.jwtDecoderService.getId();

  const completedExamData: Omit<CompletedExam, 'id'> = {
    userId: userId ?? 'unknown-user',
    examId: this.exam.id ?? 0,
    trainingId: this.exam.trainingId,
    requiredScore: this.exam.passingScore || 100,
    score: scorePercentage,
    completedDate: new Date(),
  };

  this.examStateService.finishExam();
  this.completedExamService.saveCompletedExam(completedExamData as CompletedExam).subscribe({
    next: (savedExam) => {
      this.isSubmitting = false;

      if (savedExam.passed) {
        this.dialog.open(CertificateDialogComponent, {
          width: '600px',
          maxHeight: 'auto',
          panelClass: 'compact-dialog',
          data: {
            examName: this.exam?.examTitle || 'exam',
            issueDate: new Date().toLocaleDateString(),
            validity: '1',
            status: 'passed',
            certificateId: savedExam.certificateId // Pass certificateId
          }
        }).afterClosed().subscribe(() => {
          this.router.navigate(['/certificates'], { queryParams: { newCertificateId: savedExam.certificateId } });
        });
      } else {
        this.dialog.open(CertificateDialogComponent, {
          data: {
            examName: this.exam?.examTitle,
            status: 'failed'
          }
        }).afterClosed().subscribe(() => {
          this.router.navigate(['/exams']);
        });
      }
    },
    error: (err) => {
      alert(`Failed to save your results. Please try again later.`);
      this.isSubmitting = false;
    }
  });
}

  formatTime(seconds: number): string {
    if (seconds < 0) seconds = 0;
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
}