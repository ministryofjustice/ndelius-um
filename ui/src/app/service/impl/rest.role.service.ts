import {Injectable} from "@angular/core";
import {Transaction} from "../../model/transaction";
import {Observable} from "rxjs/Observable";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {RoleService} from "../role.service";

@Injectable()
export class RestRoleService implements RoleService{
  constructor(private http: HttpClient) {}

  roles(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(environment.api.baseurl + "roles")
  }
}
