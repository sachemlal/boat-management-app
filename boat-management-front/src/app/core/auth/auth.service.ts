import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:7070/api/auth';
  private accessTokenKey = 'accessToken';
  private refreshTokenKey = 'refreshToken';


  constructor(private http: HttpClient) {
  }

  register(data: { fullname: string; username: string; password: string }) {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  refreshToken(refreshToken: string | null): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/refresh-token`,
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
