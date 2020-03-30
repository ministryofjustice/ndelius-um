import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {GroupService} from '../group.service';
import {Group} from '../../model/group';

@Injectable()
export class RestGroupService implements GroupService {
  constructor(private http: HttpClient) {}
  groups(): Observable<Group[]> {
    return this.http.get<Group[]>(environment.api.baseurl + 'groups');
  }
}
