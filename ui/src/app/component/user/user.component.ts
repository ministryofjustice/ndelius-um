import {Component, OnInit} from "@angular/core";
import {User} from "../../model/User";
import {ActivatedRoute} from "@angular/router";
import {flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {Observable} from "rxjs/Observable";
import {AuthorisationService} from "../../service/impl/authorisation.service";

@Component({
  selector: 'user',
  templateUrl: './user.component.html'
})
export class UserComponent implements OnInit {
  loaded: boolean;

  mode: string;
  user: User;

  constructor(private route: ActivatedRoute, private service: UserService, public auth: AuthorisationService) {}

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => {
        if (params.id != null) {
          this.mode = this.auth.canUpdateUser()? 'Update': 'View';
          return this.service.user(params.id)
        } else {
          this.mode = 'Add';
          return Observable.create(new User());
        }
      }))
      .subscribe((user: User) => {
        this.user = user;
        this.loaded = true;
      });
  }

  get json() {
    return JSON.stringify(this.user);
  }
}
