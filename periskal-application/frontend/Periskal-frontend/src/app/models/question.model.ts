import { Photo } from "./photo.model";
import { Answer } from "./Answer.model";

export interface Question {
    id?: number;
    text: string;
    photos?: Photo[];
    answers?: Answer[]; 
  }