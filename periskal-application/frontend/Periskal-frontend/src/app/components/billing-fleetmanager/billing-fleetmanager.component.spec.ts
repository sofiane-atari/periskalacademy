import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingFleetmanagerComponent } from './billing-fleetmanager.component';

describe('BillingFleetmanagerComponent', () => {
  let component: BillingFleetmanagerComponent;
  let fixture: ComponentFixture<BillingFleetmanagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BillingFleetmanagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BillingFleetmanagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
