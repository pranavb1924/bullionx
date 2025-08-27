import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { RegisterRequest } from '../../../core/auth.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  };
  
  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.success.set(response.message);
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Registration failed');
      }
    });
  }

  private validateForm(): boolean {
    if (!this.registerData.firstName || !this.registerData.lastName) {
      this.error.set('Please enter your full name');
      return false;
    }
    if (!this.registerData.email || !this.registerData.email.includes('@')) {
      this.error.set('Please enter a valid email');
      return false;
    }
    if (!this.registerData.password || this.registerData.password.length < 8) {
      this.error.set('Password must be at least 8 characters');
      return false;
    }
    return true;
  }
}