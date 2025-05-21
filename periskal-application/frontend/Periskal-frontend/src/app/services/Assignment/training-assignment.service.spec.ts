import { TestBed } from '@angular/core/testing';

import { TrainingAssignmentService } from './training-assignment.service';

describe('TrainingAssignmentService', () => {
  let service: TrainingAssignmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrainingAssignmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
