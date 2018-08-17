import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {AliasService} from "../alias.service";
import {Alias} from "../../model/alias";

@Injectable()
export class RestAliasService implements AliasService {
  constructor(private http: HttpClient) {}

  update(alias: Alias): Observable<void> {
    return this.http.post<void>(environment.api.baseurl + "alias/" + alias.username, alias);
  }
}
