import {Transaction} from "./transaction";

export class User {
  username: string;
  forenames: string;
  surname: string;
  staffCode: string;
  transactions: Transaction[];
}
