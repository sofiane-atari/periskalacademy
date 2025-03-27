import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingEmployeeComponent } from './training-employee.component';

describe('TrainingEmployeeComponent', () => {
  let component: TrainingEmployeeComponent;
  let fixture: ComponentFixture<TrainingEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingEmployeeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainingEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
