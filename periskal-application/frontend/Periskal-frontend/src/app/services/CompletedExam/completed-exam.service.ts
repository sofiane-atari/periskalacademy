import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';
import { CompletedExam } from '../../models/completed-exam.model';
import { ExamService } from '../Exam/exam.service';
import { TrainingService } from '../Training/training.service';

@Injectable({
  providedIn: 'root'
})
export class CompletedExamService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private examService: ExamService,
    private trainingService: TrainingService
  ) {}

  getUserCompletedExams(): Observable<CompletedExam[]> {
    return this.http.get<CompletedExam[]>(`${this.baseUrl}/completed-exams/user`).pipe(
      switchMap(exams => this.enhanceCompletedExams(exams))
    );
  }
  

  private enhanceCompletedExams(completedExams: CompletedExam[]): Observable<CompletedExam[]> {
    if (completedExams.length === 0) {
      return of([]);
    }


    const trainingIds = [...new Set(completedExams.map(exam => exam.trainingId))];
    const examIds = [...new Set(completedExams.map(exam => exam.examId))];
    

    const trainingRequests = trainingIds.map(id => 
      this.trainingService.getTrainingById(id).pipe(
        map(training => ({ id, name: this.getTrainingName(training) }))
      )
    );
    
    const examRequests = examIds.map(id => 
      this.examService.getExamById(id).pipe(
        map(exam => ({ id, name: exam.examTitle }))
      )
    );
    

    return forkJoin([
      forkJoin(trainingRequests),
      forkJoin(examRequests)
    ]).pipe(
      map(([trainings, exams]) => {

        const trainingMap = new Map(trainings.map(t => [t.id, t.name]));
        const examMap = new Map(exams.map(e => [e.id, e.name]));
        

        return completedExams.map(completedExam => ({
          ...completedExam,
          examName: examMap.get(completedExam.examId) || 'Unknown Exam',
          trainingName: trainingMap.get(completedExam.trainingId) || 'Unknown Training'
        }));
      })
    );
  }
  

  private getTrainingName(training: any): string {
    return training.nameEn || 'Unknown Training';
  }
}
