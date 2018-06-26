import {User} from "../model/User";
import {Observable} from "rxjs/Observable";

export abstract class UserService {
  abstract whoami(): Observable<User>;
  abstract users(query: string, page: number): Observable<User[]>;
  abstract user(username: string): Observable<User>;
}
