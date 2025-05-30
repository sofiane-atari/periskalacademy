export interface UserExamAttempt {
  id: number;
  userId: string;
  examId: string;
  totalAttempts: number;
  usedAttempts: number;
}