import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Boat, BoatService} from '../boat.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-edit-boat',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './edit-boat.component.html',
  styleUrl: './edit-boat.component.css'
})
export class EditBoatComponent  implements OnInit {
  boat : Boat = {
    id: 0,
    name: '',
    description: ''
  };

  constructor(private boatService: BoatService, private router: Router) {}

  ngOnInit(): void {
    const boatId = Number(this.router.url.split('/')[2]);
    this.boatService.getBoatById(boatId).subscribe({
      next: (data : Boat) => {
        this.boat = data;
      },
      error: (err) => {}
    })
  }

  onSubmit() {
    this.boatService.editBoat(this.boat).subscribe({
      next: (response) => {
        this.router.navigate(['/home']); // Navigate back to home
      },
      error: (err) => {
        console.error('Error creating boat', err);
      }
    });
  }
}
