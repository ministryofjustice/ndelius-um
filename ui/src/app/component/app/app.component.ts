import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {User} from "../../model/user";
import {NavigationEnd, NavigationStart, Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title: string = "NDelius User Management";
  loaded: boolean;
  static me: User;
  static globalMessage: string;
  static globalMessageSeverity: string = "info";

  constructor(private service: UserService, private router: Router) {}

  ngOnInit() {
    this.service.whoami().subscribe((res: User) => {
      AppComponent.me = res;
      this.loaded = true;
    });

    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        AppComponent.globalMessage = null;
      }
      if (event instanceof NavigationEnd) {
        window.scrollTo(0, 0);
      }
    });
  }

  get globalMessage(){
    return AppComponent.globalMessage;
  }

  get globalMessageSeverity(){
    return AppComponent.globalMessageSeverity;
  }

  get me() {
    return AppComponent.me;
  }
}
