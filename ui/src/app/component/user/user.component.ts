import {Component, OnInit} from "@angular/core";
import {User} from "../../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {flatMap} from "rxjs/operators";
import {UserService} from "../../service/user.service";
import {Observable} from "rxjs/Observable";
import {AuthorisationService} from "../../service/impl/authorisation.service";
import {RoleService} from "../../service/role.service";
import {Role} from "../../model/role";
import {Dataset} from "../../model/dataset";
import {DatasetService} from "../../service/dataset.service";
import {Team} from "../../model/team";
import {TeamService} from "../../service/team.service";
import {RoleGroup} from "../../model/role-group";
import {Organisation} from "../../model/organisation";

@Component({
  selector: 'user',
  templateUrl: './user.component.html'
})
export class UserComponent implements OnInit {
  loaded: boolean;

  mode: string;
  user: User;
  teams: Team[];
  datasets: Dataset[];
  roles: Role[];
  roleGroups: RoleGroup[];
  selectedGroup: RoleGroup;
  organisations: Organisation[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private roleService: RoleService,
    private datasetService: DatasetService,
    private teamService: TeamService,
    public auth: AuthorisationService) {}

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => {
        if (params.id != null) {
          this.mode = this.auth.canUpdateUser()? 'Update': 'View';
          return this.userService.read(params.id)
        } else {
          this.mode = 'Add';
          return Observable.of(new User());
        }
      }))
      .subscribe((user: User) => {
        this.user = user;
        this.loaded = true;
      });

    this.roleService.groups().subscribe((roleGroups: RoleGroup[]) => {
      this.roleGroups = roleGroups;
    });

    this.roleService.roles().subscribe((roles: Role[]) => {
      this.roles = roles;
    });

    this.datasetService.datasets().subscribe((datasets: Dataset[]) => {
      this.datasets = datasets;
    });

    this.teamService.teams().subscribe((teams: Team[]) => {
      this.teams = teams;
    });
  }

  addGroup(): void {
    if (this.selectedGroup != null) {
      if (this.user.roles == null) this.user.roles = [];
      this.roleService.group(this.selectedGroup.name).subscribe((group: RoleGroup) => {
        let userRoleNames = this.user.roles.map(r => r.name);
        this.user.roles.push(...group.roles.filter(role => userRoleNames.indexOf(role.name) === -1));
      });
    }
  }

  submit(): void {
    if (this.mode === 'Add') {
      this.userService.create(this.user).subscribe(() => {
        this.router.navigate(["/user/" + this.user.username]);
      });
    }
  }

  teamToLabel(item: Team): string {
    return item.description + ' - ' + item.code;
  }

  datasetToLabel(item: Dataset): string {
    return (item.description != null? item.description + ' - ': '') + item.code;
  }

  roleToLabel(item: Role): string {
    return item.description + ' - ' + item.name;
  }

  roleGroupToLabel(item: RoleGroup): string {
    return item.name;
  }

  get json() {
    return JSON.stringify(this.user);
  }
}
