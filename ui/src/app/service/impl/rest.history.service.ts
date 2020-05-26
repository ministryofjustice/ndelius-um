import {Injectable} from '@angular/core';
import 'rxjs/add/observable/of';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {HistoryService} from '../history.service';
import {UserHistoryItem} from '../../model/user-history-item';

@Injectable()
export class RestHistoryService implements HistoryService {
  constructor(private http: HttpClient) {}

  getHistory(username: string) {
    return this.http.get<UserHistoryItem[]>(environment.api.baseurl + 'user/' + username + '/history');
  }
}
