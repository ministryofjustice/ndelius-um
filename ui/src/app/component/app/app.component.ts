import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {User} from "../../model/user";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title: string = "NDelius User Management";
  me: User;
  static globalMessage: string;
  static globalMessageSeverity: string = "info";

  constructor(private service: UserService) {}

  ngOnInit() {
    this.service.whoami().subscribe((res: User) => this.me = res);
  }

  get globalMessage(){
    return AppComponent.globalMessage;
  }

  get globalMessageSeverity(){
    return AppComponent.globalMessageSeverity;
  }
}
