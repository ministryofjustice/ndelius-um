import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Organisation} from "../../model/organisation";
import {OrganisationService} from "../organisation.service";

@Injectable()
export class RestOrganisationService implements OrganisationService {
  constructor(private http: HttpClient) {}

  organisations(): Observable<Organisation[]> {
    return this.http.get<Organisation[]>(environment.api.baseurl + "organisations")
  }
}
