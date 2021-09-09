import {Observable} from 'rxjs';
import {Role} from '../model/role';
import {RoleGroup} from '../model/role-group';

export abstract class RoleService {
  abstract roles(): Observable<Role[]>;
  abstract groups(): Observable<RoleGroup[]>;
  abstract group(name: string): Observable<RoleGroup>;
}
