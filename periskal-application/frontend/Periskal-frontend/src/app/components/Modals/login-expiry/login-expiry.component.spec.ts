import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginExpiryComponent } from './login-expiry.component';

describe('LoginExpiryComponent', () => {
  let component: LoginExpiryComponent;
  let fixture: ComponentFixture<LoginExpiryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginExpiryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginExpiryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
