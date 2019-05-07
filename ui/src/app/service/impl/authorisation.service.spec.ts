import {AuthorisationService} from './authorisation.service';
import {User} from '../../model/user';
import {Role} from '../../model/role';

describe('AuthorisationService', () => {
  let service: AuthorisationService;

  beforeEach(() => { service = new AuthorisationService(); });

  it('should reflect the correct roles for my user', () => {
    service.me = new User();
    service.me.roles = [new Role()];
    service.me.roles[0].interactions = [];
    service.me.roles[0].interactions.push('my-role-1', 'my-role-2');

    expect(service.hasRole('my-role-1')).toBeTruthy();
    expect(service.hasRole('my-role-2')).toBeTruthy();
    expect(service.hasRole('my-role-3')).toBeFalsy();
  });
});
