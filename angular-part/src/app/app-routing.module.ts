import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataPageComponent } from './data-page/data-page.component';
import { LoginPageComponent } from './login-page/login-page.component';


const routes: Routes = [
  {
    path: 'public', component: DataPageComponent, data: {url: '/api/public/data'}
  },
  {
    path: 'private', component: DataPageComponent, data: {url: '/api/data'},
  },
  {
    path: 'login', component: LoginPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
