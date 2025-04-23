import { TestBed } from '@angular/core/testing';

import { SkipperService } from './skipper.service';

describe('SkipperService', () => {
  let service: SkipperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SkipperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
