import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {User} from "../../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {debounceTime, distinctUntilChanged, filter, flatMap, tap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {AuthorisationService} from "../../service/impl/authorisation.service";
import {NgModel} from "@angular/forms";

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
  hasMoreResults: boolean;
  searching: boolean;
  noResults: boolean;

  constructor(private route: ActivatedRoute, public router: Router, private service: UserService, public auth: AuthorisationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.pipe(
      filter(params => params.q != null && params.q !== ""),
      tap(() => this.searching = true),
      flatMap(params => this.service.search(
        this.query = params.q,
        this.page = +params.page || 1
      ))
    ).subscribe(users => {
      this.users = users;
      this.hasMoreResults = users.length >= 10;
      this.noResults = users.length === 0;
      this.searching = false;
    });
  }

  ngAfterViewInit(): void {
    this.queryInput.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(() => this.search());
  }

  search() {
    this.router.navigate(['/search'], { queryParams: { q: this.query } });
  }

  nextPage() {
    this.router.navigate(['/search'], { queryParams: { q: this.query, page: this.page+1 } });
  }

  prevPage() {
    this.router.navigate(['/search'], { queryParams: { q: this.query, page: this.page-1 } });
  }
}
