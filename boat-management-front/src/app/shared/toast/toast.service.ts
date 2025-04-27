import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  private toastSubject = new BehaviorSubject<{ message: string, type: 'success' | 'error' } | null>(null);
  toast$ = this.toastSubject.asObservable();

  showSuccess(message: string) {
    this.toastSubject.next({ message, type: 'success' });
    setTimeout(() => this.clear(), 3000); // Auto-dismiss after 3s
  }

  showError(message: string) {
    this.toastSubject.next({ message, type: 'error' });
    setTimeout(() => this.clear(), 3000);
  }

  clear() {
    this.toastSubject.next(null);
  }
}
