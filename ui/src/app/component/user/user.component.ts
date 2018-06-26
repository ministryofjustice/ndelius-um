import {Component, OnInit} from "@angular/core";
import {User} from "../../model/User";
import {ActivatedRoute} from "@angular/router";
import {flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";

@Component({
  selector: 'user',
  templateUrl: './user.component.html'
})
export class UserComponent implements OnInit {
  user: User;

  constructor(private route: ActivatedRoute, private service: UserService) {}

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => this.service.user(params.id)))
      .subscribe((user: User) => this.user = user);
  }
}
