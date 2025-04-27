import { Component } from '@angular/core';
import {ReactiveFormsModule, FormBuilder, Validators, FormGroup} from '@angular/forms';
import { CommonModule } from '@angular/common';
import {AuthService} from '../core/auth/auth.service';
import {Router} from '@angular/router';
import {ToastComponent} from '../shared/toast/toast.component';
import {ToastService} from '../shared/toast/toast.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ToastComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  loginForm: FormGroup;
  errorMessage = '';
  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private toastService: ToastService) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }


  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          const token = response.accessToken;

          if (token) {
            localStorage.setItem('accessToken', token); // Save token to localStorage
            localStorage.setItem('refreshToken', response.refreshToken);
            this.router.navigate(['/home']);
          } else {
            this.errorMessage = 'Invalid response: Token not found.';
          }
        },
        error: (errorResponse) => {
          this.errorMessage = errorResponse.message;
          this.toastService.showError(errorResponse.error.message);
        }
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  onLinkClick(event: Event): void {
    event.preventDefault();
    this.router.navigate(['/register']);
  }

  get toast$() {
    return this.toastService.toast$;
  }
}
