import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {GroupService} from '../group.service';
import {Groups} from '../../model/groups';
import {Group} from '../../model/group';

@Injectable()
export class RestGroupService implements GroupService {
  constructor(private http: HttpClient) {}

  groups(): Observable<Groups> {
    return this.http.get<Groups>(environment.api.baseurl + 'groups');
  }

  groupsByType(type: string): Observable<Group[]> {
    return this.http.get<Group[]>(environment.api.baseurl + 'groups/' + type);
  }
}
