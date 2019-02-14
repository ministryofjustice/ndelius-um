import {Role} from "./role";
import {Dataset} from "./dataset";
import {Team} from "./team";
import {StaffGrade} from "./staff-grade";

export class User {
  username: string;
  aliasUsername: string;
  forenames: string;
  surname: string;
  staffCode: string;
  staffGrade: StaffGrade;
  privateSector: boolean;
  homeArea: Dataset;
  teams: Team[];
  datasets: Dataset[];
  roles: Role[];
  startDate: Date = new Date();
  endDate: Date;
  sources: string[];
  email: string;
}
