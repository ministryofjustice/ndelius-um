import {Observable} from 'rxjs/Observable';
import {Dataset} from '../model/dataset';

export abstract class DatasetService {
  abstract datasets(): Observable<Dataset[]>;
  abstract establishments(): Observable<Dataset[]>;
  abstract nextStaffCode(datasetCode: string): Observable<string>;
  abstract subContractedProviders(datasetCode: string): Observable<Dataset[]>;
}
