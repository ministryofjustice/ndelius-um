import {ErrorInterceptor} from './error.interceptor';
import {HttpErrorResponse, HttpRequest, HttpResponse} from '@angular/common/http';
import {of, throwError} from 'rxjs';
import {AppComponent} from '../component/app/app.component';
import {environment} from '../../environments/environment';

describe('ErrorInterceptor', () => {

  it('should parse validation errors', () => {
    const message = ErrorInterceptor.parseErrorResponse(new HttpErrorResponse({
      status: 400,
      error: {
        error: ['Message 1', 'Message 2']
      }
    }));
    expect(message).toEqual('<h5>Validation Errors</h5>' +
      'Message 1</br/>' +
      'Message 2');
  });

  it('should parse validation errors with no body', () => {
    const message = ErrorInterceptor.parseErrorResponse(new HttpErrorResponse({
      status: 400
    }));
    expect(message).toEqual('<h5>Error 400: Bad Request</h5>');
  });

  it('should parse authentication failures', () => {
    const message = ErrorInterceptor.parseErrorResponse(new HttpErrorResponse({
      status: 401
    }));
    expect(message).toEqual('<h5>Error 401: Unauthorized</h5>Your session has expired. Please login again.');
  });

  it('should parse authorisation failures', () => {
    const message = ErrorInterceptor.parseErrorResponse(new HttpErrorResponse({
      status: 403,
      error: {
        requiredRoles: ['ROLE1', 'ROLE2']
      }
    }));
    expect(message).toEqual('<h5>Error 403: Forbidden</h5>Access denied. Missing roles: ROLE1, ROLE2');
  });

  it('should handle unknown error codes', () => {
    const message = ErrorInterceptor.parseErrorResponse(new HttpErrorResponse({
      status: 999
    }));
    expect(message).toEqual('<h5>Error 999: Unknown</h5>');
  });

  it('should intercept error responses', (done) => {
    new ErrorInterceptor().intercept(new HttpRequest<any>('GET', 'someurl'), {
      handle: () => throwError(new HttpErrorResponse({status: 500}))
    }).subscribe(fail, () => {
      expect(AppComponent.globalMessage).toBe('<h5>Error 500: Internal Server Error</h5>');
      done();
    });
  });
});

