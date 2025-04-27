import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

export interface Boat {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class BoatService {

  private apiUrl = 'http://localhost:7070/api/boat';

  constructor(private http: HttpClient) {}

  getBoats(): Observable<Boat[]> {
    return this.http.get<Boat[]>(this.apiUrl);
  }

  getBoatById(boatId: number): Observable<Boat> {
    return this.http.get<Boat>(this.apiUrl + `/${boatId}`);
  }

  createBoat(boat: Boat): Observable<Object> {
    return this.http.post(this.apiUrl , boat)
  }

  editBoat(boat: Boat): Observable<Object> {
    return this.http.patch(this.apiUrl + `/${boat.id}`, boat)
  }

  deleteBoat(boatId: number): Observable<Object> {
    return this.http.delete(this.apiUrl + `/${boatId}`)
  }


}
