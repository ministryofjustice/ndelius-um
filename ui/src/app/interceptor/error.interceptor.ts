import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponseBase,
} from '@angular/common/http';
import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {tap} from "rxjs/operators";
import {AppComponent} from "../component/app/app.component";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(tap(()=>{},(res: HttpResponseBase) => {
      if (res instanceof HttpErrorResponse) {
        let error: string = JSON.stringify(res.error);
        if (res.status == 401) {
          error = 'Session expired. Please refresh the page to login again.'
        } else if (res.status == 403) {
          error = 'Access denied.' + (res.error.requiredRoles instanceof Array? ' Missing roles: ' + res.error.requiredRoles.join(', '): '');
        } else if (res.error.error instanceof Array) {
          error = res.error.error.join(", ");
        }

        AppComponent.globalMessage = "Error " + res.status + " | " + error;
        AppComponent.globalMessageSeverity = "danger";
        window.scrollTo(0, 0);
      }
    }));
  }
}
