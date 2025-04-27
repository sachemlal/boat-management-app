import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateBoatComponent } from './create-boat.component';

describe('CreateBoatComponent', () => {
  let component: CreateBoatComponent;
  let fixture: ComponentFixture<CreateBoatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateBoatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateBoatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
