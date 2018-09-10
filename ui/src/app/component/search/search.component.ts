import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {User} from "../../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {debounceTime, distinctUntilChanged, filter, flatMap, tap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {AuthorisationService} from "../../service/impl/authorisation.service";
import {NgModel} from "@angular/forms";
import {forkJoin, of} from "rxjs";

@Component({
  selector: 'search',
  providers: [AuthorisationService],
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit, AfterViewInit {
  @ViewChild('queryInput') queryInput: NgModel;
  query: string = "";
  page: number;

  users: User[] = [];
  searching: boolean;
  noResults: boolean;
  hasMoreResults: boolean = true;
  previousQuery: string = "";
  searchId: number = 0;

  constructor(private route: ActivatedRoute, public router: Router, private service: UserService, public auth: AuthorisationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.pipe(
      filter(params => params.q != null && params.q !== ""),
      tap(() => this.searching = true),
      flatMap(params => forkJoin(of(++this.searchId), this.service.search(
        this.query = params.q,
        this.page = +params.page || 1
      )))
    ).subscribe(value => {
      let id = value[0];
      let users: User[] = value[1];
      if(id != this.searchId) return;
      this.hasMoreResults = users.length !== 0;
      if(this.previousQuery != this.query){
        this.previousQuery = this.query;
        this.users = users;
      } else{
        this.users.push(...users);
      }
      this.noResults = this.users.length === 0;
      this.searching = false;
    });
  }

  ngAfterViewInit(): void {
    this.queryInput.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(() => this.search());
  }

  search() {
    this.router.navigate(['/search'], {queryParams: {q: this.query}});
  }

  nextPage() {
    this.router.navigate(['/search'], {queryParams: {q: this.query, page: this.page + 1}});
  }
}
