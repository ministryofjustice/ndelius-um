import {User} from "../model/user";
import {Observable} from "rxjs/Observable";

export abstract class UserService {
  abstract whoami(): Observable<User>;
  abstract search(query: string, page: number): Observable<User[]>;
  abstract create(user: User): Observable<User>;
  abstract read(username: string): Observable<User>;
}
