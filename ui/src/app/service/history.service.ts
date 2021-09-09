import {Observable} from 'rxjs';
import {UserHistoryItem} from '../model/user-history-item';

export abstract class HistoryService {
  abstract getHistory(username: string): Observable<UserHistoryItem[]>;
}
