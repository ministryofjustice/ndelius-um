import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {User} from '../../model/user';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, filter, map, switchMap, tap} from 'rxjs/operators';
import {UserService} from '../../service/user.service';
import {AuthorisationService} from '../../service/impl/authorisation.service';
import {NgModel} from '@angular/forms';
import {merge, Observable, Subject} from 'rxjs';
import {RecentUsersUtils} from '../../util/recent-users.utils';
import {Team} from '../../model/team';
import {GroupService} from '../../service/group.service';
import {DatasetService} from '../../service/dataset.service';
import {LabelMappingUtils} from 'src/app/util/label-mapping.utils';
import {SearchParams} from '../../model/search-params';

@Component({
  selector: 'search',
  providers: [AuthorisationService],
  templateUrl: './search.component.html'
})
export class SearchComponent implements AfterViewInit {
  // utils
  LabelMappingUtils = LabelMappingUtils;
  RecentUsersUtils = RecentUsersUtils;

  // inputs
  @ViewChild('form') form: NgModel;
  searchParams = new SearchParams();
  searchSubject = new Subject();  // used to trigger the search observable programmatically
  newPageSubject = new Subject();

  // async results
  fileshareGroups = this.groupService.groupsByType('Fileshare');
  reportingGroups = this.groupService.groupsByType('NDMIS-Reporting');
  datasets = this.datasetService.datasets();
  results: Observable<User[]>;

  // state
  searching: boolean;
  hasMoreResults = true;
  prevPages: User[] = [];

  constructor(public auth: AuthorisationService,
              public router: Router,
              private route: ActivatedRoute,
              private service: UserService,
              private groupService: GroupService,
              private datasetService: DatasetService) {
  }

  ngAfterViewInit(): void {
    // Set up search results observable
    this.results = merge(
      merge(
        this.searchSubject,                                               // on form submit
        this.form.valueChanges.pipe(filter(() => this.form.dirty)),       // on user input
        this.route.queryParams.pipe(                                      // on url parameter change
          filter(params => params.q != null && params.q !== ''),          //  ignore empty param
          filter(params => params.q !== this.searchParams.query),         //  ignore when unchanged
          tap(params => this.searchParams.query = params.q)               //  grab the value from url param
        )
      ).pipe(tap(() => this.searchParams.page = 1)),                      // reset to first page for the above events
      this.newPageSubject.pipe(tap(() => this.searchParams.page++)),      // increment page on new page event
    ).pipe(
      debounceTime(500),                                                  // throttle searches
      tap(() => this.searching = true),                                   // set searching flag (for loading indicator)
      switchMap(() => this.service.search(this.searchParams)),            // perform search
      tap(page => this.hasMoreResults = page.length !== 0),               // check if there are any more pages to load
      map(page => this.prevPages =                                        // append to any previous results
        this.searchParams.page === 1 ? page : [...this.prevPages, ...page]), // (except when loading the first page)
      tap(() => this.router.navigate(['/search'],                         // update query parameter in the url
        {queryParams: {q: this.searchParams.query}})),
      tap(() => this.searching = false, () => this.searching = false)     // clear searching flag
    );
  }

  teamDescriptions(teams: Team[]): string {
    return (teams || []).map(t => t.description).join('\n');
  }
}
