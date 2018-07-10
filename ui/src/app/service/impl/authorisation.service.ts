import {Injectable} from "@angular/core";
import {UserService} from "../user.service";
import {User} from "../../model/user";
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
      return currentUser.roles.indexOf(role) !== -1
    }));
  }

  canAddUser(): Observable<boolean> {
    return this.hasRole(AuthorisationService.ADD_USER_ROLE);
  }
}
