import {Transaction} from "./transaction";
import {Dataset} from "./dataset";

export class User {
  username: string;
  forenames: string;
  surname: string;
  staffCode: string;
  transactions: Transaction[] = [];
  datasets: Dataset[] = [];
}
