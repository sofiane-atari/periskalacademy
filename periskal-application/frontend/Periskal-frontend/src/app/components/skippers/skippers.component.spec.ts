import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkippersComponent } from './skippers.component';

describe('SkippersComponent', () => {
  let component: SkippersComponent;
  let fixture: ComponentFixture<SkippersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SkippersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SkippersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
