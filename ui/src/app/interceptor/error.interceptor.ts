import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponseBase
} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Injectable} from '@angular/core';
import {tap} from 'rxjs/operators';
import {AppComponent} from '../component/app/app.component';
import {environment} from '../../environments/environment';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  private static HTTP_STATUS_CODES = {
    200 : 'OK',
    201 : 'Created',
    202 : 'Accepted',
    203 : 'Non-Authoritative Information',
    204 : 'No Content',
    205 : 'Reset Content',
    206 : 'Partial Content',
    300 : 'Multiple Choices',
    301 : 'Moved Permanently',
    302 : 'Found',
    303 : 'See Other',
    304 : 'Not Modified',
    305 : 'Use Proxy',
    307 : 'Temporary Redirect',
    400 : 'Bad Request',
    401 : 'Unauthorized',
    402 : 'Payment Required',
    403 : 'Forbidden',
    404 : 'Not Found',
    405 : 'Method Not Allowed',
    406 : 'Not Acceptable',
    407 : 'Proxy Authentication Required',
    408 : 'Request Timeout',
    409 : 'Conflict',
    410 : 'Gone',
    411 : 'Length Required',
    412 : 'Precondition Failed',
    413 : 'Request Entity Too Large',
    414 : 'Request-URI Too Long',
    415 : 'Unsupported Media Type',
    416 : 'Requested Range Not Satisfiable',
    417 : 'Expectation Failed',
    500 : 'Internal Server Error',
    501 : 'Not Implemented',
    502 : 'Bad Gateway',
    503 : 'Service Unavailable',
    504 : 'Gateway Timeout',
    505 : 'HTTP Version Not Supported'
  };

  constructor() {}

  static parseErrorResponse(res: HttpErrorResponse): string {
    let error: string = res.error ? JSON.stringify(res.error) : '';
    let header: string = res.status + ' ' + (ErrorInterceptor.HTTP_STATUS_CODES[res.status] || 'Unknown');
    if (res.status === 400 && res.error != null && res.error.error instanceof Array) {
      header = 'Validation Errors';
    }
    if (res.status === 401) {
      error = 'Your session has expired. Please login again.';
    } else if (res.status === 403) {
      error = 'Access denied.' + (res.error.requiredRoles instanceof Array ? ' Missing roles: ' + res.error.requiredRoles.join(', ') : '');
    } else if (res.error != null && res.error.error instanceof Array) {
      error = res.error.error.join('</br/>');
    }

    return '<h5>' + header + '</h5>' + error;
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(tap(() => {}, (res: HttpResponseBase) => {
      if (res instanceof HttpErrorResponse
        && req.url.indexOf(environment.api.baseurl + 'staff/') === -1) {
        AppComponent.error(ErrorInterceptor.parseErrorResponse(res));
        window.scrollTo(0, 0);
      }
    }));
  }
}
