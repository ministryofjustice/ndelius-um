import {User} from "../model/user";
import {Observable} from "rxjs/Observable";

export abstract class UserService {
  abstract whoami(): Observable<User>;
  abstract search(query: string, page: number, includeInactiveUsers: boolean): Observable<User[]>;
  abstract create(user: User): Observable<void>;
  abstract read(username: string): Observable<User>;
  abstract readByStaffCode(staffCode: string): Observable<User>;
  abstract update(username: string, user: User): Observable<void>;
}
