import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Boat, BoatService} from '../boat.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-show-boat',
  imports: [CommonModule],
  templateUrl: './show-boat.component.html',
  styleUrl: './show-boat.component.css'
})
export class ShowBoatComponent implements OnInit {
  boat: Boat | undefined;

  constructor(private boatService: BoatService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const boatId = Number(this.route.snapshot.paramMap.get('id'));
    this.boatService.getBoatById(boatId).subscribe({
      next: (data : Boat) => {
        this.boat = data;
      },
      error: (err) => {
      }
    });
  }
}
