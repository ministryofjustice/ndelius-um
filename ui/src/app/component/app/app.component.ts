import {Component, OnInit} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NavigationStart, Router} from '@angular/router';
import {AuthorisationService} from '../../service/impl/authorisation.service';
import {OAuthService, UrlHelperService} from 'angular-oauth2-oidc';
import {formatDate} from '@angular/common';
import {saveAs} from 'file-saver';
import {finalize} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  static globalMessage: string;
  static globalMessageSeverity = 'info';

  title = 'User Management';
  loaded: boolean;

  exporting: boolean;

  constructor(public service: UserService,
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

  ngOnInit() {
    const params = this.urlHelper.parseQueryString(location.search.replace(/^\?/, ''));
    if (params.hasOwnProperty('error')) {
      AppComponent.error(params['error_description'] + ' (' + params['error'] + ')');
      this.loaded = true;
      return;
    }

    if (this.oauthService.hasValidAccessToken()) {
      // If we have a valid token, load the current user details
      this.auth.loadUser().subscribe(() => this.loaded = true);
    } else {
      // Otherwise, perform login
      this.auth.login();
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

  exportUsers() {
    const timestamp = formatDate(new Date(), 'yyyyMMdd\'T\'HHmmss', 'en-GB');
    this.exporting = true;
    this.service.exportAllToCSV()
      .pipe(finalize(() => this.exporting = false))
      .subscribe(file => saveAs(file, 'DeliusUsers-' + timestamp + '.csv'));
  }

  get globalMessage() {
    return AppComponent.globalMessage;
  }

  get globalMessageSeverity() {
    return AppComponent.globalMessageSeverity;
  }
}
