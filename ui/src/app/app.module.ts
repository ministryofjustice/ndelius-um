import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {LoginInterceptor} from "./interceptor/login.interceptor";
import {environment} from "../environments/environment";

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: environment.production? []: [{
    provide: HTTP_INTERCEPTORS,
    useClass: LoginInterceptor,
    multi: true,
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
