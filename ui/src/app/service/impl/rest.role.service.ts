import {Injectable} from '@angular/core';
import {Role} from '../../model/role';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {RoleService} from '../role.service';
import {RoleGroup} from '../../model/role-group';

@Injectable()
export class RestRoleService implements RoleService {
  constructor(private http: HttpClient) {}

  roles(): Observable<Role[]> {
    return this.http.get<Role[]>(environment.api.baseurl + 'roles');
  }

  groups(): Observable<RoleGroup[]> {
    return this.http.get<RoleGroup[]>(environment.api.baseurl + 'rolegroups');
  }

  group(name: string): Observable<RoleGroup> {
    return this.http.get<RoleGroup>(environment.api.baseurl + 'rolegroup/' + name);
  }
}
