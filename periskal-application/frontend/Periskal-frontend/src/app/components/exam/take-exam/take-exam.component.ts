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

@Component({
  selector: 'app-take-exam',
  templateUrl: './take-exam.component.html',
  standalone: true,
  imports: [TranslatePipe],
  styleUrls: ['./take-exam.component.css']
})
export class TakeExamComponent implements OnInit, OnDestroy {
  exam: Exam | null = null;
  currentQuestionIndex = 0;
  loading = true;
  error: string | null = null;
  isAssigned: boolean = false;

  remainingTime: number = 0;
  private submissionInitiatedByTimer: boolean = false;
  private isSubmitting: boolean = false;

  private timerSubscriptions: Subscription = new Subscription();

  selectedAnswers: number[] = [];

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService,
    private router: Router,
    private jwtDecoderService: JwtDecoderService,
    private completedExamService: CompletedExamService,
    private examStateService: ExamStateService,
  ) { }

  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam(examId);

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
          console.log('Exam timer ran out. Initiating automatic submission.');
          this.submissionInitiatedByTimer = true;
          this.submitExam();
        } else {
          console.warn('Timer expired, but submission is already in progress.');
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
        console.log('Full exam data:', exam);
        exam.questions = exam.questions || [];
        const shuffledQuestions = exam.questions.sort(() => 0.5 - Math.random());
        const selectedQuestions = shuffledQuestions.slice(0, exam.totalQuestions);
        exam.questions = selectedQuestions;

        this.exam = exam;
        this.loading = false;

        const examDuration = this.exam.duration * 60; // in minutes
        if (this.exam.id)
          this.examStateService.startOrResumeExam(this.exam.id, examDuration);

      },
      error: (err) => {
        console.error('Failed to load exam:', err);
        this.error = 'Failed to load exam questions';
        this.loading = false;
        this.examStateService.resetTimerState(true);
      }
    });
  }

  get currentQuestion(): Question | null {
    if (!this.exam || !this.exam.questions || !this.exam.questions.length) return null;
    return this.exam.questions[this.currentQuestionIndex];
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

  selectAnswer(questionIndex: number, answerIndex: number) {
    this.selectedAnswers[questionIndex] = answerIndex;
  }

  submitExam(): void {
    if (!this.exam) {
      return;
    }

    if (this.isSubmitting) {
      console.warn('Exam submission is already in progress or completed. Preventing re-submission.');
      return;
    }

    this.isSubmitting = true;

    console.log('Current exam object before submission:', JSON.stringify(this.exam, null, 2));
    if (this.exam.trainingId === undefined || this.exam.trainingId === null) {
      console.error('CRITICAL: this.exam.trainingId IS UNDEFINED OR NULL!');
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
        console.log('Exam submitted and results saved:', savedExam);
        this.isSubmitting = false;

        const passedMessage = savedExam.passed
          ? `Congratulations! You passed the exam with a score of ${scorePercentage}%.`
          : `You did not pass the exam. The required score is ${savedExam.requiredScore}%. Please try again.`;

        const certificateMessage = savedExam.certificateId
          ? `\n\nA certificate has been generated and is available in your certificates list.`
          : '';

        alert(`${passedMessage}${certificateMessage}`);

        if (savedExam.certificateId) {
          this.router.navigate(['/certificates'], {
            queryParams: { newCertificateId: savedExam.certificateId }
          });
        } else {
          this.router.navigate(['/exams']);
        }
      },
      error: (err) => {
        console.error('Failed to save completed exam:', err);
        alert('Failed to save your results. Please try again later.');
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