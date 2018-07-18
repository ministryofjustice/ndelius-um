import {Component, OnInit} from "@angular/core";
import {User} from "../../model/user";
import {ActivatedRoute} from "@angular/router";
import {flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {Observable} from "rxjs/Observable";
import {AuthorisationService} from "../../service/impl/authorisation.service";
import {RoleService} from "../../service/role.service";
import {Transaction} from "../../model/transaction";

@Component({
  selector: 'user',
  templateUrl: './user.component.html'
})
export class UserComponent implements OnInit {
  loaded: boolean;

  mode: string;
  user: User;
  transactions: Transaction[];

  constructor(private route: ActivatedRoute, private userService: UserService, public auth: AuthorisationService, private roleService: RoleService) {}

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => {
        if (params.id != null) {
          this.mode = this.auth.canUpdateUser()? 'Update': 'View';
          return this.userService.user(params.id)
        } else {
          this.mode = 'Add';
          return Observable.create(new User());
        }
      }))
      .subscribe((user: User) => {
        this.user = user;
        this.loaded = true;
      });

    this.roleService.roles().subscribe((transactions: Transaction[]) => {
      this.transactions = transactions;
    })
  }

  get json() {
    return JSON.stringify(this.user);
  }

  get transactionNamesForDisplay(){
    return this.user.transactions.map(t => t.name + '('+ t.description +')').join(' , ')
  }
}
