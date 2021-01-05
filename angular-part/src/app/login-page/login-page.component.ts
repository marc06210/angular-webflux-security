import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent {

  credentials = {username: '', password: ''};
  errorS=null;

  constructor(private http: HttpClient, private location: Location) {
  }

  login(): boolean {

    this.http.post('/api/login', this.credentials, {observe: 'response'})
      .subscribe(obs => {
        this.location.back();
      },
      err => {
        this.errorS = 'Error login in';
      });
    return false;
  }
}
