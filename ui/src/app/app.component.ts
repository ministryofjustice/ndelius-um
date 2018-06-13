import { Component } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title: string = "NDelius User Management";
  me: string;
  constructor(private http:HttpClient) {
    http.get(environment.api.baseurl + "whoami")
      .subscribe(
        res => this.me = res['username'],
        error => console.error(error));
  }
}
