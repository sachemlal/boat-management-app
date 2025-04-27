import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowBoatComponent } from './show-boat.component';

describe('ShowBoatComponent', () => {
  let component: ShowBoatComponent;
  let fixture: ComponentFixture<ShowBoatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowBoatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShowBoatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
