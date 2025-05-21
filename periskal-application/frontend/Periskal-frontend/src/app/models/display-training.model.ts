import { Training } from './training.model';

export interface DisplayTraining extends Training {
  isAssigned?: boolean; 
  assignedDate?: Date | string | null; 

}