import {Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {HomeComponent} from './home/home.component';
import {AuthGuard} from './core/auth/auth.guard';
import {ShowBoatComponent} from './boat/show-boat/show-boat.component';
import {CreateBoatComponent} from './boat/create-boat/create-boat.component';
import {EditBoatComponent} from './boat/edit-boat/edit-boat.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard]},
  { path: 'boat/create', component: CreateBoatComponent, canActivate: [AuthGuard]},
  { path: 'boat/:id', component: ShowBoatComponent, canActivate: [AuthGuard]},
  { path: 'boat/:id/edit', component: EditBoatComponent, canActivate: [AuthGuard]},
  { path: '**', redirectTo: 'home' }
];
