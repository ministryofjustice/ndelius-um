import {Observable} from "rxjs/Observable";
import {Team} from "../model/team";

export abstract class TeamService {
  abstract teams(): Observable<Team[]>;
}
