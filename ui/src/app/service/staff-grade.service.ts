import {Observable} from 'rxjs/Observable';
import {StaffGrade} from '../model/staff-grade';

export abstract class StaffGradeService {
  abstract staffGrades(): Observable<StaffGrade[]>;
}
