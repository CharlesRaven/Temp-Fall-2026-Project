import { TestBed } from '@angular/core/testing';
import { DegreeProgressService } from './degree-progress';

describe('DegreeProgress', () => {
 let service: DegreeProgressService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DegreeProgressService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
