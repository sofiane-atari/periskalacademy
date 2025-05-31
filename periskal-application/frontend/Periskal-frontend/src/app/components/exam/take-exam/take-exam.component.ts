import { Component, OnInit, OnDestroy } from '@angular/core';
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
import { ConfirmPromptComponent } from '../../confirm-prompt/confirm-prompt.component';
import { CertificateDialogComponent } from '../certificatedialog/certificatedialog.component';

@Component({
  selector: 'app-take-exam',
  templateUrl: './take-exam.component.html',
  standalone: true,
  imports: [TranslatePipe],
  styleUrls: ['./take-exam.component.css'],
})
export class TakeExamComponent implements OnInit, OnDestroy {
  exam: Exam | null = null;
  currentQuestionIndex = 0;
  loading = true;
  error: string | null = null;
  isAssigned: boolean = false;
  selectedAnswers: (number | undefined)[] = [];
  public lang: string = localStorage.getItem('lang') || 'en';

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

  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam(examId);

    this.timerSubscriptions.add(
      this.examStateService.remainingTime$.subscribe((time) => {
        this.remainingTime = time;
      })
    );

    this.timerSubscriptions.add(
      this.examStateService.examFinished$
        .pipe(filter((finished) => finished))
        .subscribe(() => {
          if (!this.isSubmitting) {
            this.submissionInitiatedByTimer = true;
            this.submitExam();
          }
        })
    );
  }

  ngOnDestroy(): void {
    this.timerSubscriptions.unsubscribe();
  }

  loadExam(examId: number): void {
    this.loading = true;
    this.error = null;

    this.examService.getExamById(examId).subscribe({
      next: (exam) => {
        exam.questions = exam.questions || [];
        const shuffledQuestions = exam.questions.sort(
          () => 0.5 - Math.random()
        );
        const selectedQuestions = shuffledQuestions.slice(
          0,
          exam.totalQuestions
        );
        exam.questions = selectedQuestions;

        this.exam = exam;
        this.loading = false;

        const examDuration = this.exam.duration * 60; // in minutes
        if (this.exam.id)
          this.examStateService.startOrResumeExam(this.exam.id, examDuration);
      },
      error: (err) => {
        this.error = 'Failed to load exam questions';
        this.loading = false;
        this.examStateService.resetTimerState(true);
      },
    });
  }

  get currentQuestion(): Question | null {
    if (!this.exam || !this.exam.questions || !this.exam.questions.length)
      return null;
    return this.exam.questions[this.currentQuestionIndex];
  }

  clearSelection() {
    this.selectedAnswers[this.currentQuestionIndex] = undefined;
  }

  nextQuestion(): void {
    if (
      this.exam &&
      this.exam.questions &&
      this.currentQuestionIndex < this.exam.questions.length - 1
    ) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  goToQuestion(index: number): void {
    if (
      this.exam &&
      this.exam.questions &&
      index >= 0 &&
      index < this.exam.questions.length
    ) {
      this.currentQuestionIndex = index;
    }
  }

  selectAnswer(questionIndex: number, answerIndex: number) {
    this.selectedAnswers[questionIndex] = answerIndex;
  }

  submitExam(): void {
    if (!this.exam) {
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    if (this.exam.trainingId === undefined || this.exam.trainingId === null) {
      alert(
        'Error: Cannot submit exam. Training ID is missing from exam data. Please contact support.'
      );
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
    const scorePercentage =
      totalQuestions > 0
        ? Math.round((correctCount / totalQuestions) * 100)
        : 0;
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
    console.log(completedExamData);
    this.completedExamService
      .saveCompletedExam(completedExamData as CompletedExam)
      .subscribe({
        next: (savedExam) => {
          this.isSubmitting = false;

          const passedMessage = savedExam.passed
            ? `Congratulations! You passed the exam with a score of ${scorePercentage}%.`
            : `You did not pass the exam. The required score is ${savedExam.requiredScore}%. Please try again.`;

          const certificateMessage = savedExam.certificateId
            ? `\n\nA certificate has been generated and is available in your certificates list.`
            : '';

          if (savedExam.passed) {
            const dialogRef = this.dialog
              .open(CertificateDialogComponent, {
                width: '600px',
                maxHeight: 'auto',
                panelClass: 'compact-dialog',
                data: {
                  examName: this.exam?.examTitleEn || 'exam',
                  issueDate: new Date().toLocaleDateString(),
                  validity: '1',
                  status: 'passed',
                },
              })
              .afterClosed()
              .subscribe(() => {
                this.router.navigate(['/modules']);
              });
          } else {
            this.dialog.open(CertificateDialogComponent, {
              data: {
                examName: this.exam?.examTitleEn,
                status: 'failed',
              },
            });
          }

          if (savedExam.certificateId) {
            this.router.navigate(['/certificates'], {
              queryParams: { newCertificateId: savedExam.certificateId },
            });
          } else {
            this.router.navigate(['/exams']);
          }
        },
        error: (err) => {
          alert('Failed to save your results. Please try again later.');
          this.isSubmitting = false;
        },
      });
  }

  formatTime(seconds: number): string {
    if (seconds < 0) seconds = 0;
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}`;
  }

  getExamTitle(): string {
  if (!this.exam) return 'Unknown exam';
  
  switch(this.lang) {
    case 'en': return this.exam.examTitleEn || '';
    case 'nl': return this.exam.examTitleNl || '';
    case 'fr': return this.exam.examTitleFr || '';
    case 'de': return this.exam.examTitleDe || '';
    default: return this.exam.examTitleEn || 'Unknown exam';
  }
}

  getExamDescription(): string {
    if (!this.exam) return '';
    
    switch(this.lang) {
      case 'en': return this.exam.examDescriptionEn || '';
      case 'nl': return this.exam.examDescriptionNl || '';
      case 'fr': return this.exam.examDescriptionFr || '';
      case 'de': return this.exam.examDescriptionDe || '';
      default: return this.exam.examDescriptionEn || '';
    }
  }

  getQuestionText(question: Question): string {
    switch(this.lang) {
      case 'en': return question.textEn || '';
      case 'nl': return question.textNl || '';
      case 'fr': return question.textFr || '';
      case 'de': return question.textDe || '';
      default: return question.textEn || '';
    }
  }

  getAnswerText(answer: any): string {
    switch(this.lang) {
      case 'en': return answer.textEn || '';
      case 'nl': return answer.textNl || '';
      case 'fr': return answer.textFr || '';
      case 'de': return answer.textDe || '';
      default: return answer.textEn || '';
    }
  }
}
