import {User} from "../model/user";
import {Observable} from "rxjs/Observable";

export abstract class UserService {
  abstract whoami(): Observable<User>;
  abstract search(query: string, page: number): Observable<User[]>;
  abstract create(user: User): Observable<void>;
  abstract read(username: string): Observable<User>;
  abstract update(user: User): Observable<void>;
}
