import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {

  loginForm: any;  // ✅ define property first

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    // ✅ initialize inside constructor
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  submit() {
  if (this.loginForm.invalid) return;

  this.auth.login(this.loginForm.value).subscribe({
    next: (res: any) => {
      // Persist values the rest of app expects
      // Adjust keys to match what other parts of app read (token, role, fcx_name/email)
      if (res.token) localStorage.setItem('token', res.token);
      if (res.role) localStorage.setItem('role', res.role);
      if (res.name) localStorage.setItem('fcx_name', res.name);
      if (res.email) localStorage.setItem('fcx_email', res.email);

      alert('Login Successful ✅');

      // Route based on role
      if (res.role === 'ROLE_ADMIN') {
        this.router.navigate(['/admin']);
      } else if (res.role === 'ROLE_SELLER') {
        this.router.navigate(['/seller/dashboard']);
      } else {
        // default to user dashboard
        this.router.navigate(['/dashboard']);
      }
    },
    error: () => alert('Invalid Email or Password ❌')
  });
}

}
