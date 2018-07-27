import {Observable} from "rxjs/Observable";
import {Organisation} from "../model/organisation";

export abstract class OrganisationService {
  abstract organisations(): Observable<Organisation[]>;
}
