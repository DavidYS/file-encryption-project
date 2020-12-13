import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpHeaders, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserCredentials } from '../models/userCredentials';

@Injectable({
  providedIn: 'root'
})
export class ApiRepository {

  private baseUrl = 'http://localhost:8080';

  constructor(private httpClient: HttpClient) { }

  upload(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.httpClient.request(req);
  }

  getFiles(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/files`);
  }

  signUp = (userCredentials: UserCredentials) =>
    this.httpClient.post(this.baseUrl + '/sign-up', userCredentials);

  login = (userCredentials: UserCredentials) =>
    this.httpClient.post(this.baseUrl + '/login', userCredentials);

  getUsers = () =>
    this.httpClient.get(this.baseUrl + '/users');
}
