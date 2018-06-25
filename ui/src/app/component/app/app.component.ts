import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title: string = "NDelius User Management";
  me: string;

  constructor(private service: UserService) {}

  ngOnInit() {
    this.service.whoami().subscribe(res => this.me = res['username']);
  }
}
