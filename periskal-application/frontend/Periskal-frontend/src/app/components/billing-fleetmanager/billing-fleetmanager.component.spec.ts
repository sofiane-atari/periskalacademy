import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FleetManagerBillingComponent } from './billing-fleetmanager.component';


describe('BillingFleetmanagerComponent', () => {
  let component: FleetManagerBillingComponent;
  let fixture: ComponentFixture<FleetManagerBillingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FleetManagerBillingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FleetManagerBillingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
