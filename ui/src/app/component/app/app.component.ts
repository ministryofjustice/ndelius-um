import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title: string = "NDelius User Management";
  me: string;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http
      .get(environment.api.baseurl + "whoami")
      .subscribe(res => this.me = res['username']);
  }
}
