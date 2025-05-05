import { Photo } from "./photo.model";

export interface Question {
    id?: number;
    text: string;
    photos?: Photo[];
  }