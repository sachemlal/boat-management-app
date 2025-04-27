import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AsyncPipe, NgIf} from '@angular/common';
import {AuthService} from '../core/auth/auth.service';
import {Router} from '@angular/router';
import {ToastComponent} from '../shared/toast/toast.component';
import {ToastService} from '../shared/toast/toast.service';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    ToastComponent,
    AsyncPipe
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';
  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private toastService: ToastService) {
    this.registerForm = this.fb.group({
      fullname: ['', [Validators.required]],
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  get toast$() {
    return this.toastService.toast$;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: (response : any) => {

          if(response.status === 400){
            this.toastService.showError(response.message);
          }
          else {
            this.toastService.showSuccess('User registered successfully. Please login to continue.');
            this.router.navigate(['/login']);
          }

        },
        error: (errorResponse) => {
          this.errorMessage = errorResponse.message;
          this.toastService.showError(errorResponse.error.message);
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }
}
