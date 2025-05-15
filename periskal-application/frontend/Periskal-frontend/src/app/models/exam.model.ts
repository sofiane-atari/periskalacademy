import { Question } from "./question.model";

export interface Exam {
    id?: number; // Optional because it's assigned by the backend
    trainingId: number;
    examTitle: string;
    examDescription: string;
    totalQuestions: number;
    passingScore?: number; // optional for now until we can enter it during exam creation
    questions: Question[];
}