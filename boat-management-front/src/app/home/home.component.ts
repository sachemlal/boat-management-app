import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Boat, BoatService} from '../boat/boat.service';
import {CommonModule} from '@angular/common';
import {ToastComponent} from '../shared/toast/toast.component';
import {ToastService} from '../shared/toast/toast.service';

@Component({
  standalone: true,
  selector: 'app-home',
  imports: [CommonModule, ToastComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  boats: Boat[] = [];
  constructor(private boatService: BoatService, private router: Router,  private toastService: ToastService) {}

  ngOnInit(): void {
    this.loadBoats()
  }

  /**
   * Load boats from the api
   */
  loadBoats(): void {
    this.boatService.getBoats().subscribe((boats: Boat[]) => {
      this.boats = boats;
    });
  }

  get toast$() {
    return this.toastService.toast$;
  }

  /**
   * Navigate to boat page
   * @param boat
   */
  onBoatClick(boat: Boat): void {
    this.router.navigate([`/boat/${boat.id}`]);
  }

  /**
   * Navigate to create boat page
   */
  onCreateBoat() {
    // Navigate to create boat page
    this.router.navigate(['/boat/create']);
  }

  /**
   * Navigate to edit boat page
   * @param boatId
   */
  onEditBoat(boatId: number) {
    // Navigate to create boat page
    this.router.navigate([`/boat/${boatId}/edit`]);
  }

  /**
   * Delete a boat
   * @param event
   * @param boatId
   */
  onDeleteBoat(event: Event, boatId: number) {
    if (confirm('Are you sure you want to delete this boat?')) {
      this.boatService.deleteBoat(boatId).subscribe({
        next: () => {
          this.loadBoats();
          this.toastService.showSuccess('Boat deleted successfully!');
        },
        error: (err) => {
          console.error('Error deleting boat', err);
          this.toastService.showError('Failed to delete boat.');
        }
      });
    }
  }

}
