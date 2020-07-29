import {Role} from './role';
import {Dataset} from './dataset';
import {Team} from './team';
import {StaffGrade} from './staff-grade';
import {UserHistoryItem} from './user-history-item';
import {Groups} from './groups';

export class User {
  username: string;
  forenames: string;
  surname: string;
  email: string;
  telephoneNumber: string;
  staffCode: string;
  staffGrade: StaffGrade;
  privateSector: boolean;
  homeArea: Dataset;
  teams: Team[];
  datasets: Dataset[];
  establishments: Dataset[];
  roles: Role[];
  groups: Groups = new Groups();
  subContractedProvider: Dataset;
  startDate: Date = new Date();
  endDate: Date;
  created: UserHistoryItem;
  updated: UserHistoryItem;
  changeNote: string;
  sources: string[];
}
