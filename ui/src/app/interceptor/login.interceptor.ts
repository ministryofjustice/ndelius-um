import {HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse,} from '@angular/common/http';
import {Observable} from "rxjs/Observable";
import {environment} from "../../environments/environment";
import {Injectable} from "@angular/core";
import {flatMap} from "rxjs/operators";

/**
 * Only used for development, where UI is served separately. Intercepts HTTP requests to perform authentication where required.
 *
 * Note: This behaviour is performed automatically by the browser when the UI is bundled with the API.
 */
@Injectable()
export class LoginInterceptor implements HttpInterceptor {
  static token: string;
  user = {
    name: 'test.user',
    pass: 'secret'
  };

  constructor(private http: HttpClient) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.endsWith("login")) return next.handle(req);

    if (LoginInterceptor.token != null) {
      return LoginInterceptor.appendToken(req, next);
    } else {
      console.log("In development mode, authenticating as", this.user.name);

      return this.http.post(environment.api.baseurl + "login", "", {
        headers: {"Authorization": "Basic " + btoa(this.user.name + ":" + this.user.pass)}
      }).pipe(flatMap((res: HttpResponse<any>) => {
        console.log("Storing auth token", res);
        LoginInterceptor.token = res['token'];
        return LoginInterceptor.appendToken(req, next)
      }));
    }
  }

  private static appendToken(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.token)
    }));
  }
}
