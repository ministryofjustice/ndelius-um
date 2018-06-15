import {Component, Input, OnInit, Output} from "@angular/core";
import {User} from "../../model/User";
import {ActivatedRoute, Router} from "@angular/router";
import {filter, flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";

@Component({
  selector: 'search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {
  @Input()  query: string = "";
  @Output() users: User[] = [];

  page: number = 1;

  constructor(private route: ActivatedRoute, private router: Router, private service: UserService) {}

  ngOnInit(): void {
    this.route.queryParams.pipe(
      flatMap(params => this.service.users(
        this.query = params.q,
        this.page = params.page || 1
      ))
    ).subscribe(users => this.users = users);
  }

  search() {
    this.router.navigate(['/search'], { queryParams: { q: this.query } });
  }
}
