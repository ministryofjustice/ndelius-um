import {Observable} from 'rxjs';
import {Organisation} from '../model/organisation';

export abstract class OrganisationService {
  abstract organisations(): Observable<Organisation[]>;
}
