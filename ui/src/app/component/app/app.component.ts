import {Component, OnInit} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NavigationStart, Router} from '@angular/router';
import {AuthorisationService} from '../../service/impl/authorisation.service';
import {OAuthService} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  static globalMessage: string;
  static globalMessageSeverity = 'info';

  title = 'User Management';
  loaded: boolean;

  constructor(private service: UserService,
              public auth: AuthorisationService,
              private router: Router,
              private oauthService: OAuthService) {}

  static error(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = 'danger';
  }

  static info(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = 'info';
  }

  static success(message: string) {
    AppComponent.globalMessage = message;
    AppComponent.globalMessageSeverity = 'success';
  }

  static hideMessage() {
    AppComponent.globalMessage = null;
  }

  ngOnInit() {
    // Login if required, then fetch current user details
    this.oauthService.configure(environment.authConfig);
    this.oauthService.tryLoginCodeFlow()
      .then(_ => this.service.whoami().subscribe(me => {
        this.auth.me = me;
        this.loaded = true;
      }));

    this.router.routeReuseStrategy.shouldReuseRoute = (future, curr) => {
      return future.routeConfig === curr.routeConfig
        && (future.routeConfig == null || future.routeConfig.path !== 'user/:id');
    };
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        AppComponent.hideMessage();
      }
    });
  }

  get globalMessage() {
    return AppComponent.globalMessage;
  }

  get globalMessageSeverity() {
    return AppComponent.globalMessageSeverity;
  }
}
