// take-exam.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../../services/Exam/exam.service';
import { Exam } from '../../../models/exam.model';
import { Question } from '../../../models/question.model';
import { TranslatePipe } from '@ngx-translate/core';
import { CompletedExam } from '../../../models/completed-exam.model';
import { JwtDecoderService } from '../../../services/JwtDecode/jwt-decoder.service';
import { CompletedExamService } from '../../../services/CompletedExam/completed-exam.service';

@Component({
  selector: 'app-take-exam',
  templateUrl: './take-exam.component.html',
  imports: [TranslatePipe],
  styleUrls: ['./take-exam.component.css']
})
export class TakeExamComponent implements OnInit {
  exam: Exam | null = null;
  currentQuestionIndex = 0;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private examService: ExamService,
    private router: Router,
    private jwtDecoderService :JwtDecoderService,
    private completedExamService: CompletedExamService
  ) { }

  ngOnInit(): void {
    const examId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadExam(examId);
  }


  loadExam(examId: number): void {
    this.loading = true;
    this.error = null;

    this.examService.getExamById(examId).subscribe({
      next: (exam) => {
        console.log('Answers van eerste vraag:', exam.questions[0].answers);


        console.log('Full exam data:', exam);
        console.log('First question photos:', exam.questions[0]?.photos);
        if (exam.questions[0]?.photos?.length) {
          console.log('First photo data:', exam.questions[0].photos[0].imageData);
        }

        const shuffledQuestions = exam.questions.sort(() => 0.5 - Math.random());
        const selectedQuestions = shuffledQuestions.slice(0, exam.totalQuestions);
        exam.questions = selectedQuestions;

        this.exam = exam;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load exam:', err);
        this.error = 'Failed to load exam questions';
        this.loading = false;
      }
  });
}

  get currentQuestion(): Question | null {
    if (!this.exam || !this.exam.questions.length) return null;
    return this.exam.questions[this.currentQuestionIndex];
  }

  nextQuestion(): void {
    if (this.exam && this.currentQuestionIndex < this.exam.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  goToQuestion(index: number): void {
    if (this.exam && index >= 0 && index < this.exam.questions.length) {
      this.currentQuestionIndex = index;
    }
  }

  selectedAnswers: number[] = [];

  selectAnswer(questionIndex: number, answerIndex: number) {
    this.selectedAnswers[questionIndex] = answerIndex;
  }


  submitExam(): void {

    if (!this.exam) {
      return;
    }

    // ****** START DEBUGGING ******
    console.log('Current exam object before submission:', JSON.stringify(this.exam, null, 2));
    if (this.exam.trainingId === undefined || this.exam.trainingId === null) {
      console.error('CRITICAL: this.exam.trainingId IS UNDEFINED OR NULL!');
      alert('Error: Cannot submit exam. Training ID is missing from exam data. Please contact support.');
      return; // Stop submission
    }
    // ****** END DEBUGGING ******

    let correctCount = 0

    this.exam.questions.forEach((question, index) => {
      const selected = this.selectedAnswers[index];
      if (selected !== undefined && selected + 1 === question.correctAnswer) {
        correctCount++;
      }
    });

    const totalQuestions = this.exam.questions.length;
    const scorePercentage = Math.round((correctCount / totalQuestions) * 100);
    const userId = this.jwtDecoderService.getId(); 

 

    // Prepare completed exam data with actual passing score from exam
    const completedExamData: Omit<CompletedExam, 'id'> = { 
      userId: userId ?? 'unknown-user',
      examId: this.exam.id ?? 0, 
      trainingId: this.exam.trainingId, 
      requiredScore: this.exam.passingScore || 100, // Use exam's passing score with fallback to 100
      score: scorePercentage,
      completedDate: new Date(),
    };

    //call service to save completed exam
    this.completedExamService.saveCompletedExam(completedExamData as CompletedExam).subscribe({
      next: (savedExam) => {
        console.log('Exam submitted and results saved:', savedExam);
        
        // Check if exam was passed and a certificate was generated
        const passedMessage = savedExam.passed 
          ? `Congratulations! You passed the exam with a score of ${scorePercentage}%.`
          : `You did not pass the exam. The required score is ${savedExam.requiredScore}%. Please try again.`;
        
        const certificateMessage = savedExam.certificateId 
          ? `\n\nA certificate has been generated and is available in your certificates list.`
          : '';
          
        alert(`${passedMessage}${certificateMessage}`);
        
        // Navigate based on result - if they passed and got a certificate, go to certificates page with query parameter
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
      }
    });
  }
}