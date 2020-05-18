export class UserHistoryItem {
  by: {
    username: string;
    forenames: string;
    surname: string;
  };
  at: Date;
  note?: string;
}
