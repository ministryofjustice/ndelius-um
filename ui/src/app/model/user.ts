import {Role} from "./role";
import {Dataset} from "./dataset";
import {Team} from "./team";

export class User {
  username: string;
  forenames: string;
  surname: string;
  staffCode: string;
  homeArea: string;
  teams: Team[] = [];
  datasets: Dataset[] = [];
  roles: Role[] = [];
  inNationalDelius: boolean;
  inPrimaryAD: boolean;
  inSecondaryAD: boolean;
}
