export class UserHistoryItem {
  user: {
    username: string;
    forenames: string;
    surname: string;
  };
  time: Date;
  note?: string;
}
