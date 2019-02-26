import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {DatasetService} from "../dataset.service";
import {Dataset} from "../../model/dataset";

@Injectable()
export class RestDatasetService implements DatasetService {
  constructor(private http: HttpClient) {}

  datasets(): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(environment.api.baseurl + "datasets")
  }

  nextStaffCode(datasetCode: string): Observable<string> {
    return this.http.get(environment.api.baseurl + "dataset/" + datasetCode + "/nextStaffCode", {
      responseType: 'text'
    })
  }

  subContractedProviders(datasetCode: string): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(environment.api.baseurl + "dataset/" + datasetCode + "/subContractedProviders")
  }
}
