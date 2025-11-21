import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {
  email = '';
  password = '';
  isLoggingIn = false;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  submit() {
    if (!this.email || !this.password) {
      alert('Please enter email and password');
      return;
    }

    this.isLoggingIn = true;

    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.isLoggingIn = false;
        // make sure getRole() returns the role synchronously or from stored token
        const role = this.auth.getRole();
        if (role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else if (role === 'ROLE_SELLER') {
          this.router.navigate(['/seller/dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.isLoggingIn = false;
        alert('Wrong email or password');
      }
    });
  }
}
