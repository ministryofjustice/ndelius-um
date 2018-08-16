import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {User} from "../../model/user";
import {NavigationStart, Router} from "@angular/router";

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

    this.router.routeReuseStrategy.shouldReuseRoute = (future, curr) => {
      return future.routeConfig == curr.routeConfig
        && (future.routeConfig == null || future.routeConfig.path != "user/:id");
    };
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        AppComponent.globalMessage = null;
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
