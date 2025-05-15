export interface CompletedExam {
    id: number;
    userId: string;
    trainingId: number;
    examId: number;
    requiredScore: number;
    score: number;
    completedDate: Date;
    
    certificateId?: number;
    passed?: boolean;
    
    examName?: string;
    trainingName?: string;
}
