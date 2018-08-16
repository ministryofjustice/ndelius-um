import {Component, OnInit} from '@angular/core';
import {UserService} from "../../service/user.service";
import {NavigationStart, Router} from "@angular/router";
import {AuthorisationService} from "../../service/impl/authorisation.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title: string = "NDelius User Management";
  loaded: boolean;
  static globalMessage: string;
  static globalMessageSeverity: string = "info";

  constructor(private service: UserService, public auth: AuthorisationService, private router: Router) {}

  ngOnInit() {
    this.service.whoami().subscribe(me => {
      this.auth.me = me;
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

  static error(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = "danger";
  }

  static info(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = "info";
  }

  static success(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = "success";
  }

  static hideMessage() {
    AppComponent.globalMessage = null;
  }
}
