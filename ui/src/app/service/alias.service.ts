import {Observable} from "rxjs/Observable";
import {Alias} from "../model/alias";

export abstract class AliasService {
  abstract update(alias: Alias): Observable<void>;
}
