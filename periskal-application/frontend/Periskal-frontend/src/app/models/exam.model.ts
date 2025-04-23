import { Question } from "./question.model";

export interface Exam {
    id?: number; // Optional because it's assigned by the backend
    trainingId: number;
    examTitle: string;
    examDescription: string;
    questions: Question[];
  }