import {Injectable} from "@angular/core";
import {User} from "../model/User";
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class UserService {
  constructor(private http: HttpClient) {
  }

  users(query: string, page: number): Observable<User[]> {
    return this.http.get<User[]>(
      environment.api.baseurl + "users",
      {
        params: {
          q: query,
          page: page.toString(),
        }
      });
  }
}
