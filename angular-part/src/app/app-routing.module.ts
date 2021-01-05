import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataPageComponent } from './data-page/data-page.component';


const routes: Routes = [
  {
    path: 'public', component: DataPageComponent, data: {url: '/api/public/data'}
  },
  {
    path: 'private', component: DataPageComponent, data: {url: '/api/data'}
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
