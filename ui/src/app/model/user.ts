import {Role} from "./role";
import {Dataset} from "./dataset";
import {Team} from "./team";
import {Organisation} from "./organisation";

export class User {
  username: string;
  aliasUsername: string;
  forenames: string;
  surname: string;
  staffCode: string;
  homeArea: Dataset;
  organisation: Organisation;
  teams: Team[];
  datasets: Dataset[];
  roles: Role[];
  startDate: Date = new Date();
  endDate: Date;
  inNationalDelius: boolean;
  inPrimaryAD: boolean;
  inSecondaryAD: boolean;
}
