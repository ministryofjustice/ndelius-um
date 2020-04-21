import {User} from '../model/user';
import {Observable} from 'rxjs/Observable';
import {SearchParams} from '../model/search-params';

export abstract class UserService {
  abstract whoami(): Observable<User>;
  abstract search(params: SearchParams): Observable<User[]>;
  abstract create(user: User): Observable<void>;
  abstract read(username: string): Observable<User>;
  abstract readByStaffCode(staffCode: string): Observable<User>;
  abstract update(username: string, user: User): Observable<void>;
}
