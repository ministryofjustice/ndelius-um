import {Injectable} from "@angular/core";
import {Role} from "../../model/role";
import {User} from "../../model/user";

@Injectable({
  providedIn: 'root',
})
export class AuthorisationService {
  static me: User;
  static SEARCH_USER_ROLE: string = 'UMBI001';
  static GET_USER_ROLE: string = 'UMBI002';
  static ADD_USER_ROLE: string = 'UMBI003';
  static UPDATE_USER_ROLE: string = 'UMBI004';

  hasRole(role: string): boolean {
    return AuthorisationService.me.roles
      .filter((t: Role) => (t.interactions || []).indexOf(role) !== -1)
      .length > 0;
  }

  canAddUser(): boolean {
    return this.hasRole(AuthorisationService.ADD_USER_ROLE);
  }

  canGetUser(): boolean {
    return this.hasRole(AuthorisationService.GET_USER_ROLE);
  }

  canSearch(): boolean {
    return this.hasRole(AuthorisationService.SEARCH_USER_ROLE);
  }

  canUpdateUser(): boolean {
    return this.hasRole(AuthorisationService.UPDATE_USER_ROLE);
  }

  canMigrateUsers(): boolean {
    return this.canUpdateUser();
  }

  get me(): User {
    return AuthorisationService.me;
  }

  set me(me: User) {
    AuthorisationService.me = me;
  }
}
