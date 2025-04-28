import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';

export interface Boat {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class BoatService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getBoats(): Observable<Boat[]> {
    return this.http.get<Boat[]>(this.apiUrl + '/boat');
  }

  getBoatById(boatId: number): Observable<Boat> {
    return this.http.get<Boat>(this.apiUrl + `/boat/${boatId}`);
  }

  createBoat(boat: Boat): Observable<Object> {
    return this.http.post(this.apiUrl + '/boat' , boat)
  }

  editBoat(boat: Boat): Observable<Object> {
    return this.http.patch(this.apiUrl + `/boat/${boat.id}`, boat)
  }

  deleteBoat(boatId: number): Observable<Object> {
    return this.http.delete(this.apiUrl + `/boat/${boatId}`)
  }

}
