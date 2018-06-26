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
        AppComponent.globalMessage = res.message;
        AppComponent.globalMessageSeverity = "danger";
      }
    }));
  }
}
