import { Component, OnInit } from '@angular/core';
import { UserCredentials } from '../../models/userCredentials';
import { Subscription } from 'rxjs';
import { ApiRepository } from '../../services/api-repository.service';
import { Router } from '@angular/router';
import { ViewChild } from "@angular/core";
import { ElementRef } from "@angular/core";
import { Renderer2 } from "@angular/core";

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent implements OnInit {

  constructor(
    private apiRepository: ApiRepository,
    private router: Router,
    private renderer: Renderer2
  ) {
  }

  @ViewChild('inputForm', { static: false }) inputFormRef: ElementRef;

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
    this.renderer.setStyle(this.inputFormRef.nativeElement, 'height', '500px');
  }

  signUp() {
    this.subscription.add(this.apiRepository.signUp(this.userCredentials).subscribe(
      (response) => {
        if (response) {
          this.errorType = 'success';
        } else {
          this.errorType = 'danger';
        }
        this.renderer.setStyle(this.inputFormRef.nativeElement, 'height', '550px');
      },
      (error) => {
        this.errorType = 'warning';
        this.renderer.setStyle(this.inputFormRef.nativeElement, 'height', '550px');
      }
    ));
  }

  login() {
    this.subscription.add(this.apiRepository.login(this.userCredentials).subscribe(
      (response) => {
        if (response) {
          sessionStorage.setItem('private', JSON.stringify(this.userCredentials.email));
          this.router.navigate(['/upload']);
        } else {
          this.invalidCredentials = true;
          this.renderer.setStyle(this.inputFormRef.nativeElement, 'height', '550px');
        }
      }
    ));
  }
}
