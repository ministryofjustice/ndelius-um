import {Observable} from 'rxjs/Observable';
import {Groups} from '../model/groups';

export abstract class GroupService {
  abstract groups(): Observable<Groups>;
}
