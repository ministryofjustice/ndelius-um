import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";
import {User} from "../../model/user";
import {environment} from "../../../environments/environment";

@Injectable()
export class RestUserService implements UserService {
  constructor(private http: HttpClient) {}

  whoami(): Observable<User> {
    return this.http.get<User>(environment.api.baseurl + "whoami")
  }

  search(query: string, page: number, includeInactiveUsers: boolean): Observable<User[]> {
    return this.http.get<User[]>(environment.api.baseurl + "users", {
      params: {
        q: query,
        page: page.toString(),
        includeInactiveUsers: String(includeInactiveUsers)
      }
    });
  }

  create(user: User): Observable<void> {
    return this.http.post<void>(environment.api.baseurl + "user", user);
  }

  read(username: string): Observable<User> {
    return this.http.get<User>(environment.api.baseurl + "user/" + username);
  }

  readByStaffCode(staffCode: string): Observable<User> {
    return this.http.get<User>(environment.api.baseurl + "staff/" + staffCode);
  }

  update(username: string, user: User): Observable<void> {
    return this.http.post<void>(environment.api.baseurl + "user/" + username, user);
  }
}
