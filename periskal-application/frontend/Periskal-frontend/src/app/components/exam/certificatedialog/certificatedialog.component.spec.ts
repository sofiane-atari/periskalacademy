import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificatedialogComponent } from './certificatedialog.component';

describe('CertificatedialogComponent', () => {
  let component: CertificatedialogComponent;
  let fixture: ComponentFixture<CertificatedialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CertificatedialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CertificatedialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
