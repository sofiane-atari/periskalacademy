import { Photo } from './photo.model';
import { Answer } from './Answer.model';

export interface Question {
  id?: number;
  textEn: string;
  textNl: string;
  textFr: string;
  textDe: string;
  correctAnswer: number;
  photos?: Photo[];
  answers?: Answer[];
}
