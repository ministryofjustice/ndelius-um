import {Role} from './role';
import {Dataset} from './dataset';
import {Team} from './team';
import {StaffGrade} from './staff-grade';
import {Modification} from './modification';
import {Group} from './group';

export class User {
  username: string;
  forenames: string;
  surname: string;
  email: string;
  staffCode: string;
  staffGrade: StaffGrade;
  privateSector: boolean;
  homeArea: Dataset;
  teams: Team[];
  datasets: Dataset[];
  establishments: Dataset[];
  roles: Role[];
  groups: {
    'Fileshare': Group[],
    'NDMIS-Reporting': Group[],
  };
  subContractedProvider: Dataset;
  startDate: Date = new Date();
  endDate: Date;
  created: Modification;
  updated: Modification;
  sources: string[];
}
