import {Observable} from 'rxjs/Observable';
import {UserHistoryItem} from '../model/user-history-item';

export abstract class HistoryService {
  abstract getHistory(username: string): Observable<UserHistoryItem[]>;
}
