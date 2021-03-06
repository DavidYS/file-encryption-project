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

  upload(file: File, encrypt: boolean): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);
    formData.append('encrypt', JSON.stringify(encrypt));
    formData.append('email', sessionStorage.getItem('private'));

    const req = new HttpRequest('POST', `${this.baseUrl}/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.httpClient.request(req);
  }

  getFiles(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/files`);
  }

  deleteFile = (filePath): Observable<any> =>  this.httpClient.delete(`${filePath}`);

  deleteFiles = () => this.httpClient.delete(this.baseUrl + '/files');

  signUp = (userCredentials: UserCredentials) =>
    this.httpClient.post(this.baseUrl + '/sign-up', userCredentials, {responseType: 'text'});

  login = (userCredentials: UserCredentials) =>
    this.httpClient.post(this.baseUrl + '/login', userCredentials);
}
