import {RecentUsersUtils} from './recent-users.utils';

describe('RecentUsersUtils', () => {
  beforeEach(() => { RecentUsersUtils.clear(); });

  it('#add should add an item to the list', () => {
    RecentUsersUtils.add('some-value');
    RecentUsersUtils.add('some-other-value');
    expect(RecentUsersUtils.getRecentUsers()).toContain('some-value');
    expect(RecentUsersUtils.getRecentUsers()).toContain('some-other-value');
  });

  it('should only keep a list of the most recently added 5 items', () => {
    RecentUsersUtils.add('a');
    RecentUsersUtils.add('b');
    RecentUsersUtils.add('c');
    RecentUsersUtils.add('d');
    RecentUsersUtils.add('e');
    RecentUsersUtils.add('f');
    expect(RecentUsersUtils.getRecentUsers().length).toBe(5);
    expect(RecentUsersUtils.getRecentUsers()).not.toContain('a');
  });

  it('should automatically remove duplicates', () => {
    RecentUsersUtils.add('a');
    RecentUsersUtils.add('a');
    RecentUsersUtils.add('a');
    expect(RecentUsersUtils.getRecentUsers().length).toBe(1);
  });

  it('#remove should remove an item from the list', () => {
    RecentUsersUtils.add('some-value');
    RecentUsersUtils.remove('some-value');
    expect(RecentUsersUtils.getRecentUsers()).toEqual([]);
  });

  it('#clear should clear the recent user list', () => {
    RecentUsersUtils.add('some-value');
    RecentUsersUtils.clear();
    expect(RecentUsersUtils.getRecentUsers()).toEqual([]);
  });
});
