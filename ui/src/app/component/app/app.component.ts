import {Component, OnInit} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NavigationStart, Router} from '@angular/router';
import {AuthorisationService} from '../../service/impl/authorisation.service';
import {OAuthService, UrlHelperService} from 'angular-oauth2-oidc';
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
              private oauthService: OAuthService,
              private urlHelper: UrlHelperService) {}

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

  private loadUser() {
    this.service.whoami().subscribe(me => {
      this.auth.me = me;
      this.loaded = true;
    });
  }

  ngOnInit() {
    // Login using OAuth if required
    this.oauthService.configure(environment.authConfig);
    if (!this.oauthService.hasValidAccessToken()) {
      if (!this.urlHelper.parseQueryString(location.search.replace(/^\?/, '')).hasOwnProperty('code')) {
        // Get authorization code first
        this.oauthService.initCodeFlow();
      } else {
        // Then get access token and reload the page
        this.oauthService.tryLoginCodeFlow().then(_ => location.reload());
      }
    } else {
      // Once we have a valid token, load the current user details
      this.loadUser();
    }

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
