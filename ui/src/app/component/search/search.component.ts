import {Component, Input, OnInit, Output} from "@angular/core";
import {User} from "../../model/User";
import {ActivatedRoute, Router} from "@angular/router";
import {flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {AuthorisationService} from "../../service/impl/authorisation.service";

@Component({
  selector: 'search',
  providers: [AuthorisationService],
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {
  @Input()  query: string = "";
  @Output() users: User[] = [];

  page: number;
  hasMoreResults: boolean = false;
  canAddUser: boolean = false;


  constructor(private route: ActivatedRoute, private router: Router, private service: UserService, private authorisationService: AuthorisationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.pipe(
      flatMap(params => this.service.users(
        this.query = params.q || "",
        this.page = +params.page || 1
      ))
    ).subscribe(users => {
      this.hasMoreResults = users.length >= 10;
      this.users = users
    });

    this.authorisationService.canAddUser().subscribe((canAddUser: boolean) => {
      console.log("Can Add User: ",canAddUser);
      this.canAddUser = canAddUser;
    })
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
