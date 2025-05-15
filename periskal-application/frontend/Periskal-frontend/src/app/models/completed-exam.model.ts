export interface CompletedExam {
    id: number;
    userId: string;
    trainingId: number;
    examId: number;
    requiredScore: number;
    score: number;
    completedDate: Date;
    
    // These will be populated from related entities
    examName?: string;
    trainingName?: string;
}
