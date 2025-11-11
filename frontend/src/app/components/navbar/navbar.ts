import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../../services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, MatToolbarModule, NgIf],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {

  // ✅ constructor MUST be inside the class
  constructor(public auth: AuthService) {}

}
