import { TestBed } from '@angular/core/testing';
import { UserAttemptService } from './user-attempt.service';


describe('UserAttemptService', () => {
  let service: UserAttemptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAttemptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
