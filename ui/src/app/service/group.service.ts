import {Observable} from 'rxjs/Observable';
import {Group} from '../model/group';

export abstract class GroupService {
  abstract groups(): Observable<Group[]>;
}
