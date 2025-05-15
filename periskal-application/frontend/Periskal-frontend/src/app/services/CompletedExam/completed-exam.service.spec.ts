import { TestBed } from '@angular/core/testing';

import { CompletedExamService } from './completed-exam.service';

describe('CompletedExamService', () => {
  let service: CompletedExamService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompletedExamService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
