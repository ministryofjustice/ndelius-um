import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NgModule} from '@angular/core';

import {AppComponent} from '../component/app/app.component';
import {SearchComponent} from "../component/search/search.component";
import {UserComponent} from "../component/user/user.component";
import {AppRoutingModule} from "./app.routing.module";
import {LoginInterceptor} from "../interceptor/login.interceptor";
import {environment} from "../../environments/environment";
import {RestUserService} from "../service/impl/rest.user.service";
import {UserService} from "../service/user.service";
import {ErrorInterceptor} from "../interceptor/error.interceptor";
import {MessageComponent} from "../component/message/message.component";

@NgModule({
  declarations: [AppComponent, SearchComponent, UserComponent, MessageComponent],
  imports: [BrowserModule, FormsModule, HttpClientModule, AppRoutingModule],
  providers: [
    {provide: UserService, useClass: RestUserService},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    ...(environment.production? []: [{provide: HTTP_INTERCEPTORS, useClass: LoginInterceptor, multi: true}])
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
