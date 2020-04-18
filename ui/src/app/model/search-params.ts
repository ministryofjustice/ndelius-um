import {Group} from './group';
import {Dataset} from './dataset';

export class SearchParams {
  query = '';
  reportingGroups: Group[] = [];
  fileshareGroups: Group[] = [];
  datasets: Dataset[] = [];
  includeInactiveUsers = false;
  page = 1;
  pageSize = 50;

  toRequestParams(): { [param: string]: string | string[] } {
    return {
      q: this.query,
      fileshareGroup: this.fileshareGroups.map(i => i.name),
      reportingGroup: this.reportingGroups.map(i => i.name),
      dataset: this.datasets.map(i => i.code),
      includeInactiveUsers: String(this.includeInactiveUsers),
      page: this.page.toString(),
      pageSize: this.pageSize.toString(),
    };
  }
}
