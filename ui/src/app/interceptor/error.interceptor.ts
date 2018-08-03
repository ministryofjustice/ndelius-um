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
        if (res.error.error instanceof Array) {
          error = res.error.error.join(", ");
        }
        if(res.error.requiredRoles instanceof Array){
          error = " Access denied. Roles required: " + res.error.requiredRoles.join(", ")
        }

        AppComponent.globalMessage = "Error " + res.status + " | " + error;
        AppComponent.globalMessageSeverity = "danger";
      }
    }));
  }
}
