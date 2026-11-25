
import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

@Component({
	selector: 'app-register',
	imports: [CommonModule, FormsModule, RouterLink],
	templateUrl: './register.html',
	styleUrl: './register.css'
})
export class Register {
	private http = inject(HttpClient);
	private router = inject(Router);
	private cdr = inject(ChangeDetectorRef);

	name = '';
	email = '';
	password = '';
	confirmPassword = '';
	message = '';
	loading = false;

	register(): void {
		if (this.loading) return;

		const name = this.name.trim();
		const email = this.email.trim().toLowerCase();

		if (!name || !email || !this.password) {
			this.message = 'Please complete all fields.';
			return;
		}

		if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
			this.message = 'Please enter a valid email address.';
			return;
		}

		if (this.password.length < 8) {
			this.message = 'Password must contain at least 8 characters.';
			return;
		}

		if (this.password !== this.confirmPassword) {
			this.message = 'Passwords do not match.';
			return;
		}

		this.loading = true;
		this.message = 'Creating account...';

		this.http.post('/api/v1/auth/register', {
			name,
			email,
			password: this.password
		}).subscribe({
			next: () => {
				this.loading = false;
				this.message = 'Account created successfully!';
				this.cdr.detectChanges();
				this.router.navigate(['/login']);
			},
			error: (error) => {
				console.error('Registration error:', error);
				this.loading = false;
				this.message = error.status === 409
					? 'An account with this email already exists.'
					: 'Registration failed. Please try again.';
				this.cdr.detectChanges();
			}
		});
	}
}
