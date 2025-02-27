import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingEmployeeComponentComponent } from './training-employee-component.component';

describe('TrainingEmployeeComponentComponent', () => {
  let component: TrainingEmployeeComponentComponent;
  let fixture: ComponentFixture<TrainingEmployeeComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingEmployeeComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrainingEmployeeComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
