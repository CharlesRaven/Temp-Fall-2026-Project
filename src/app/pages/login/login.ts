
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private http = inject(HttpClient);
  private router = inject(Router);

  email = '';
  password = '';
  message = '';

  login() {
    this.message = 'Logging in...';

    this.http.post<{
      token: string;
      tokenType: string;
      expiresIn: string;
    }>('/api/v1/auth/login', {
      Email: this.email,
      Password: this.password
    }).subscribe({
      next: (response) => {
        sessionStorage.setItem('authToken', response.token);
        this.message = 'Login successful!';
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.message = 'Login failed. Please check your email and password.';
      }
    });
  }
}
