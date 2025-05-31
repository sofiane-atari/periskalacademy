import { TestBed } from '@angular/core/testing';
import { CanDeactivateFn } from '@angular/router';

import { examInProgressGuard } from './exam-in-progress.guard';

describe('examInProgressGuard', () => {
  const executeGuard: CanDeactivateFn<unknown> = (...guardParameters) => 
      TestBed.runInInjectionContext(() => examInProgressGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
