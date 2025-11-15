import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { tap } from "rxjs";

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  register(data: any) {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  login(data: any) {
    return this.http.post<any>(`${this.baseUrl}/login`, data).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);   // <-- contains ROLE_USER, ROLE_SELLER, ROLE_ADMIN
        localStorage.setItem('name', res.name);
      })
    );
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  isLoggedIn() {
    return !!localStorage.getItem('token');
  }

  getName() {
    return localStorage.getItem('name');
  }

  // ✅ new helpers below
  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isSeller(): boolean {
    return this.getRole() === 'ROLE_SELLER';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ROLE_ADMIN';
  }

  isUser(): boolean {
    return this.getRole() === 'ROLE_USER';
  }
}
