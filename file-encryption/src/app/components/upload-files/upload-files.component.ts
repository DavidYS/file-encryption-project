import { Component, OnInit } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ApiRepository } from 'src/app/services/api-repository.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subscription } from "rxjs";
import { Router } from "@angular/router";

@Component({
  selector: 'app-upload-files',
  templateUrl: './upload-files.component.html',
  styleUrls: ['./upload-files.component.css']
})
export class UploadFilesComponent implements OnInit, OnDestroy {

  selectedFiles: FileList;
  currentFile: File;
  progress = 0;
  message = '';
  action = false;

  fileName: string;
  fileInfos: Observable<any>;
  fileSelected;
  warning = false;

  private subscription = new Subscription();

  constructor(
    private uploadService: ApiRepository,
    private router: Router) {
  }

  ngOnInit() {
    this.fileInfos = this.uploadService.getFiles();
  }

  selectFile(event) {
    this.fileName = event.target.files[0].name;
    this.selectedFiles = event.target.files;
  }

  upload(encrypt: boolean) {
    this.action = true;

    if (this.selectedFiles) {
      this.currentFile = this.selectedFiles.item(0);
    }
    this.subscription.add(this.uploadService.upload(this.currentFile, encrypt).subscribe(
      event => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          this.message = event.body.message;
          this.fileInfos = this.uploadService.getFiles();
        }
      },
      err => {
        this.progress = 0;
        this.action = false;
        this.message = 'Bad key is used for decryption!';
        this.currentFile = undefined;
        this.warning = true;
      }));

    this.selectedFiles = undefined;
  }

  delete() {
    this.subscription.add(this.uploadService.deleteFile(this.fileSelected).subscribe());
  }

  logout() {
    sessionStorage.removeItem('private');
    this.subscription.add(this.uploadService.deleteFiles().subscribe());
    this.router.navigate(['home']);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
