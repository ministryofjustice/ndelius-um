import {Injectable} from "@angular/core";
import {UserService} from "../user.service";
import {User} from "../../model/user";
import {Transaction} from "../../model/transaction";
import {Observable} from "rxjs/Observable";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class AuthorisationService {
  static SEARCH_USER_ROLE:string = 'UMBI001';
  static GET_USER_ROLE:string = 'UMBI002';
  static ADD_USER_ROLE:string = 'UMBI003';

  constructor(private userService: UserService) { }

  hasRole(role: string): Observable<boolean> {
    return this.userService.whoami().pipe(map((currentUser: User) => {
      return currentUser.transactions
        .filter((t: Transaction) => t.roles.indexOf(role) !== -1)
        .length > 0;
    }));
  }

  canAddUser(): Observable<boolean> {
    return this.hasRole(AuthorisationService.ADD_USER_ROLE);
  }

  canGetUser(): Observable<boolean> {
    return this.hasRole(AuthorisationService.GET_USER_ROLE);
  }

  canSearchUser(): Observable<boolean> {
    return this.hasRole(AuthorisationService.SEARCH_USER_ROLE);
  }

}
