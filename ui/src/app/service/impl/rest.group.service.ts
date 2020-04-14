import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {GroupService} from '../group.service';
import {Groups} from '../../model/groups';

@Injectable()
export class RestGroupService implements GroupService {
  constructor(private http: HttpClient) {}
  groups(): Observable<Groups> {
    return this.http.get<Groups>(environment.api.baseurl + 'groups');
  }
}
