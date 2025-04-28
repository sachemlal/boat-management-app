import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;
  private accessTokenKey = 'accessToken';
  private refreshTokenKey = 'refreshToken';


  constructor(private http: HttpClient) {
  }

  register(data: { fullname: string; username: string; password: string }) {
    return this.http.post(`${this.apiUrl}/auth/register`, data);
  }

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, credentials, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  refreshToken(refreshToken: string | null): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/auth/refresh-token`,
      {refreshToken:  refreshToken},
      {headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getAccessToken() {
    return localStorage.getItem(this.accessTokenKey);
  }

  getRefreshToken() {
    return localStorage.getItem(this.refreshTokenKey);
  }

  logout() {
    localStorage.clear();
  }

}
