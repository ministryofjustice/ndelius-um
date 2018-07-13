import {Component, OnInit} from "@angular/core";
import {User} from "../../model/User";
import {ActivatedRoute, Router} from "@angular/router";
import {filter, flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {AuthorisationService} from "../../service/impl/authorisation.service";

@Component({
  selector: 'search',
  providers: [AuthorisationService],
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {
  query: string = "";
  page: number;
  users: User[] = [];
  hasMoreResults: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router, private service: UserService, public auth: AuthorisationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.pipe(
      filter(params => params.q != null),
      flatMap(params => this.service.users(
        this.query = params.q,
        this.page = +params.page || 1
      ))
    ).subscribe(users => {
      this.hasMoreResults = users.length >= 10;
      this.users = users;
    });
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
