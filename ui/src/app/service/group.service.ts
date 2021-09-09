import {Observable} from 'rxjs';
import {Groups} from '../model/groups';
import {Group} from '../model/group';

export abstract class GroupService {
  abstract groups(): Observable<Groups>;
  abstract groupsByType(type: string): Observable<Group[]>;
}
