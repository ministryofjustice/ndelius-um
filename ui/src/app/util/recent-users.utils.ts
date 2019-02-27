export class RecentUsersUtils {
  private static KEY: string = "recent-users";
  private static LIMIT: number = 5;

  public static getRecentUsers(): string[] {
    return JSON.parse(localStorage.getItem(this.KEY)) || [];
  }

  public static setRecentUsers(recentUsers: string[]): void {
    localStorage.setItem(this.KEY, JSON.stringify(recentUsers.slice(-this.LIMIT)));
  }

  public static add(username: string): void {
    let recentUsers = this.getRecentUsers();
    while (recentUsers.indexOf(username) > -1) recentUsers.splice(recentUsers.indexOf(username), 1);
    recentUsers.push(username);
    this.setRecentUsers(recentUsers);
  }

  public static remove(username: string): void {
    let recentUsers = this.getRecentUsers();
    while (recentUsers.indexOf(username) > -1) recentUsers.splice(recentUsers.indexOf(username), 1);
    this.setRecentUsers(recentUsers);
  }

  public static clear(): void {
    localStorage.removeItem(this.KEY);
  }
}
