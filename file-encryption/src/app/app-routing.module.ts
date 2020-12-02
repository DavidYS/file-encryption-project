import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes } from '@angular/router';
import { RouterModule } from '@angular/router';
import { LandingPageComponent } from "./components/landing-page/landing-page.component";
import { UploadFilesComponent } from "./components/upload-files/upload-files.component";

const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'upload', component: UploadFilesComponent}
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
