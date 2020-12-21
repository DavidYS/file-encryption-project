import { Component, OnInit } from '@angular/core';
import { UserCredentials } from '../../models/userCredentials';
import { Subscription } from 'rxjs';
import { ApiRepository } from '../../services/api-repository.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit {

  constructor(
    private apiRepository: ApiRepository,
    private router: Router
  ) {
  }

  errorType;
  invalidCredentials;
  isLogin = true;
  userCredentials = new UserCredentials();

  private subscription = new Subscription();

  ngOnInit(): void {
  }

  togglePage() {
    this.isLogin = !this.isLogin;
    this.invalidCredentials = false;
    this.errorType = null;
    this.userCredentials.password = null;
    this.userCredentials.email = null;
  }

  signUp() {
    this.subscription.add(this.apiRepository.signUp(this.userCredentials).subscribe(
      (response) => {
        this.errorType = 'success';
      }
    ));
  }

  login() {
    this.subscription.add(this.apiRepository.login(this.userCredentials).subscribe(
      (response) => {
        if (response) {
          sessionStorage.setItem('private', JSON.stringify(response));
          this.router.navigate(['/upload']);
        } else {
          this.invalidCredentials = true;
        }
      }
    ));
  }
}
