import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DongleLoginComponent } from './dongle-login.component';

describe('DongleLoginComponent', () => {
  let component: DongleLoginComponent;
  let fixture: ComponentFixture<DongleLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DongleLoginComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DongleLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
