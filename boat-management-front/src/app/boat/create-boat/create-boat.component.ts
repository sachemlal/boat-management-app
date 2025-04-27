import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {Router} from '@angular/router';
import {Boat, BoatService} from '../boat.service';

@Component({
  selector: 'app-create-boat',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './create-boat.component.html',
  styleUrl: './create-boat.component.css'
})
export class CreateBoatComponent {

  boat : Boat = {
    id: 0,
    name: '',
    description: ''
  };

  constructor(private boatService: BoatService, private router: Router) {}

  onSubmit() {
    this.boatService.createBoat(this.boat).subscribe({
      next: (response) => {
        this.router.navigate(['/home']); // Navigate back to home
      },
      error: (err) => {
        console.error('Error creating boat', err);
      }
    });
  }
}
