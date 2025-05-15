export interface CompletedExam {
    id: number;
    userId: string;
    trainingId: number;
    examId: number;
    requiredScore: number;
    score: number;
    completedDate: Date;
    
        
    examName?: string;
    trainingName?: string;
}
