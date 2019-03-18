import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {StaffGradeService} from '../staff-grade.service';
import {StaffGrade} from '../../model/staff-grade';

@Injectable()
export class RestStaffGradeService implements StaffGradeService {
  constructor(private http: HttpClient) {}

  staffGrades(): Observable<StaffGrade[]> {
    return this.http.get<StaffGrade[]>(environment.api.baseurl + 'staffgrades');
  }
}
