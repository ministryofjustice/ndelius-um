import {Transaction} from "./transaction";
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
  transactions: Transaction[] = [];
  inNationalDelius: boolean;
  inPrimaryAD: boolean;
  inSecondaryAD: boolean;
}
