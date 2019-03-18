import {Observable} from 'rxjs/Observable';
import {Team} from '../model/team';

export abstract class TeamService {
  abstract teams(): Observable<Team[]>;
  abstract providerTeams(provider: string): Observable<Team[]>;
}
