import {AuthorisationService} from './authorisation.service';
import {User} from '../../model/user';
import {Role} from '../../model/role';
import {TestBed} from '@angular/core/testing';
import {UserService} from '../user.service';
import {RestUserService} from './rest.user.service';
import {OAuthModule} from 'angular-oauth2-oidc';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('AuthorisationService', () => {
  let service: AuthorisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthorisationService, {provide: UserService, useClass: RestUserService}],
      imports: [HttpClientTestingModule, OAuthModule.forRoot()],
    });
    service = TestBed.get(AuthorisationService);
  });

  it('should reflect the correct roles for my user', () => {
    service.me = new User();
    service.me.roles = [new Role()];
    service.me.roles[0].interactions = [];
    service.me.roles[0].interactions.push('my-role-1', 'my-role-2');

    expect(service.hasInteraction('my-role-1')).toBeTruthy();
    expect(service.hasInteraction('my-role-2')).toBeTruthy();
    expect(service.hasInteraction('my-role-3')).toBeFalsy();
  });
});
