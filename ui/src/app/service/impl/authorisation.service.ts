import {Injectable} from '@angular/core';
import {User} from '../../model/user';
import {tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {UserService} from '../user.service';
import {OAuthService, UrlHelperService} from 'angular-oauth2-oidc';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthorisationService {
  static me: User;
  static SEARCH_USER_ROLE = 'UMBI001';
  static GET_USER_ROLE = 'UMBI002';
  static ADD_USER_ROLE = 'UMBI003';
  static UPDATE_USER_ROLE = 'UMBI004';
  static PUBLIC_ADMIN_ROLE = 'UABI020';
  static NATIONAL_USER_ROLE = 'UABT0050';

  private readonly initialQueryParams;

  constructor(
    private userService: UserService,
    private oauthService: OAuthService,
    private urlHelper: UrlHelperService
  ) {
    this.initialQueryParams = this.urlHelper.parseQueryString(location.search.replace(/^\?/, ''));
    delete this.initialQueryParams[''];
    this.oauthService.configure({customQueryParams: this.initialQueryParams, ...environment.authConfig});
    this.oauthService.setStorage({
      getItem: (key: string) => sessionStorage.getItem('umt_' + key),
      removeItem: (key: string) => sessionStorage.removeItem('umt_' + key),
      setItem: (key: string, value: string) => sessionStorage.setItem('umt_' + key, value)
    });
  }

  hasInteraction(interaction: string): boolean {
    return [].concat(...AuthorisationService.me.roles
      .map(r => (r.interactions || [])))
      .includes(interaction);
  }

  hasRole(role: string): boolean {
    return AuthorisationService.me.roles
      .map(r => r.name)
      .includes(role);
  }

  isNational(): boolean {
    return this.hasRole(AuthorisationService.NATIONAL_USER_ROLE);
  }

  canAddUser(): boolean {
    return this.hasInteraction(AuthorisationService.ADD_USER_ROLE);
  }

  canGetUser(): boolean {
    return this.hasInteraction(AuthorisationService.GET_USER_ROLE);
  }

  canSearch(): boolean {
    return this.hasInteraction(AuthorisationService.SEARCH_USER_ROLE);
  }

  canUpdateUser(): boolean {
    return this.hasInteraction(AuthorisationService.UPDATE_USER_ROLE);
  }

  canExportUsers(): boolean {
    return this.isNational();
  }

  isPublicAdmin(): boolean {
    return this.hasInteraction(AuthorisationService.PUBLIC_ADMIN_ROLE);
  }

  loadUser(): Observable<User> {
    return this.userService.whoami()
      .pipe(tap(
        (me: User) => AuthorisationService.me = me,
        err => {
          if (err instanceof HttpErrorResponse && err.status === 401) {
            this.oauthService.logOut();
            location.reload();
          }
        }));
  }

  login(): void {
    // Login using OAuth if required
    if (!this.oauthService.hasValidAccessToken()) {
      if (this.initialQueryParams.hasOwnProperty('u') && this.initialQueryParams.hasOwnProperty('t')) {
        // We have delius request params, use preauthenticated OAuth flow
        this.oauthService.customQueryParams['grant_type'] = 'preauthenticated';
        this.oauthService.fetchTokenUsingPasswordFlow(null, null).then(_ => location.reload());
      } else if (!this.initialQueryParams.hasOwnProperty('code')) {
        // Get authorization code first
        this.oauthService.initCodeFlow();
      } else {
        // Then get access token and reload the page
        this.oauthService.tryLoginCodeFlow().then(_ => location.reload());
      }
    }
  }

  get me(): User {
    return AuthorisationService.me;
  }

  set me(me: User) {
    AuthorisationService.me = me;
  }
}
