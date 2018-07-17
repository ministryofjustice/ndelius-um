import {Injectable} from "@angular/core";
import {Transaction} from "../../model/transaction";
import {AppComponent} from "../../component/app/app.component";

@Injectable({
  providedIn: 'root',
})
export class AuthorisationService {
  static SEARCH_USER_ROLE: string = 'UMBI001';
  static GET_USER_ROLE: string = 'UMBI002';
  static ADD_USER_ROLE: string = 'UMBI003';
  static UPDATE_USER_ROLE: string = 'UMBI004';
  static DELETE_USER_ROLE: string = 'UMBI005';

  hasRole(role: string): boolean {
    return AppComponent.me.transactions
      .filter((t: Transaction) => t.roles.indexOf(role) !== -1)
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
    return false;
  }
}
