import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Team} from "../../model/team";
import {TeamService} from "../team.service";

@Injectable()
export class RestTeamService implements TeamService {
  constructor(private http: HttpClient) {}

  teams(): Observable<Team[]> {
    return this.http.get<Team[]>(environment.api.baseurl + "teams");
  }

  providerTeams(provider: string): Observable<Team[]> {
    return this.http.get<Team[]>(environment.api.baseurl + "teams?provider=" + provider);
  }
}
