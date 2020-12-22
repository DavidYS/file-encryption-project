import { Component, OnInit } from '@angular/core';
import { ApiRepository } from 'src/app/services/api-repository.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Location } from '@angular/common';
import { OnDestroy } from "@angular/core";

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

  fileInfos: Observable<any>;

  constructor(
    private uploadService: ApiRepository,
    private location: Location) { }

  ngOnInit() {
    this.fileInfos = this.uploadService.getFiles();
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  upload(encrypt: boolean) {
    this.progress = 0;

    this.currentFile = this.selectedFiles.item(0);
    this.uploadService.upload(this.currentFile, encrypt).subscribe(
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
        this.message = 'Could not upload the file!';
        this.currentFile = undefined;
      });

    this.selectedFiles = undefined;
  }

  ngOnDestroy(): void {
    sessionStorage.removeItem('private');
  }
}
