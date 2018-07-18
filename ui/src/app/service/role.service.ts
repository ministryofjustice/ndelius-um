import {Observable} from "rxjs/Observable";
import {Transaction} from "../model/transaction";

export abstract class RoleService {
  abstract roles(): Observable<Transaction[]>;
}
