import {Component, OnInit} from "@angular/core";
import {User} from "../../model/user";
import {ActivatedRoute, Router} from "@angular/router";
import {flatMap, map} from "rxjs/operators";
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
import {OrganisationService} from "../../service/organisation.service";
import {StaffGrade} from "../../model/staff-grade";
import {StaffGradeService} from "../../service/staff-grade.service";
import {AppComponent} from "../app/app.component";

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
  staffGrades: StaffGrade[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private roleService: RoleService,
    private datasetService: DatasetService,
    private teamService: TeamService,
    private organisationService: OrganisationService,
    private staffGradeService: StaffGradeService,
    public auth: AuthorisationService) {}

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => {
        if (params.id != null) {
          this.mode = this.auth.canUpdateUser()? 'Update': 'View';
          return this.userService.read(params.id);
        } else {
          return this.route.queryParams.pipe(flatMap(query => {
            if (query.copy != null) {
              this.mode = 'Add';
              return this.userService.read(query.copy).pipe(map(user => {
                user.username = null;
                user.aliasUsername = null;
                user.staffCode = null;
                user.staffGrade = null;
                user.teams = [];
                return user;
              }));
            } else {
              this.mode = 'Add';
              return Observable.of(new User());
            }
          }));
        }
      }))
      .subscribe((user: User) => {
        this.user = user;
        this.addSelectableRoles(user.roles);
        this.loaded = true;
        this.homeAreaChanged();
      });

    this.roleService.groups().subscribe((roleGroups: RoleGroup[]) => {
      this.roleGroups = roleGroups;
    });

    this.roleService.roles().subscribe(roles => this.addSelectableRoles(roles));

    this.datasetService.datasets().subscribe((datasets: Dataset[]) => {
      this.datasets = datasets;
      if (this.user != null) this.user.datasets = [];
    });

    this.staffGradeService.staffGrades().subscribe((staffGrades: StaffGrade[]) => {
      this.staffGrades = staffGrades;
    });
  }

  private addSelectableRoles(roles: Role[]) {
    this.roles = this.roles || [];
    this.roles.push(...roles.filter(role => this.roles.map(r => r.name).indexOf(role.name) === -1));
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

  homeAreaChanged() {
    if (this.user.homeArea != null) {
      this.teams = null;
      this.teamService.providerTeams(this.user.homeArea.code).subscribe((teams: Team[]) => {
        this.teams = teams;
        if (this.user.teams != null) {
          this.user.teams = this.user.teams.filter(team => teams.map(team => team.code).indexOf(team.code) !== -1);
        }
      });
    } else {
      this.teams = [];
    }
  }

  submit(): void {
    if (this.mode === 'Add') {
      this.userService.create(this.user).subscribe(() => {
        this.router.navigate(["/user/" + this.user.username]).then(() => {
          AppComponent.globalMessage = "Created " + this.user.username + " successfully.";
          AppComponent.globalMessageSeverity = "info";
        });
      });
    } else if (this.mode === 'Update') {
      this.userService.update(this.user).subscribe(() => {
        this.router.navigate(["/user/" + this.user.username]).then(() => {
          AppComponent.globalMessage = "Updated " + this.user.username + " successfully.";
          AppComponent.globalMessageSeverity = "info";
        });
      });
    } else {
      console.error('Unsupported mode', this.mode);
    }
  }

  codeDescriptionToLabel(item: {code: string, description: string}): string {
    return (item.description != null? item.description + ' - ': '') + item.code;
  }

  nameDescriptionToLabel(item: {name: string, description: string}): string {
    return (item.description != null? item.description + ' - ': '') + item.name;
  }

  nameToLabel(item: {name: string}): string {
    return item.name;
  }

  sectorToLabel(item: boolean): string {
    return item? 'Private': 'Public';
  }

  get staffCodeSuffix(): string {
    let staffCode = this.user.staffCode;
    let homeArea = this.user.homeArea;
    if (staffCode == null || homeArea == null || !staffCode.startsWith(homeArea.code)) {
      return staffCode;
    } else {
      return staffCode.substring(homeArea.code.length);
    }
  }

  set staffCodeSuffix(staffCodeSuffix: string) {
    if (staffCodeSuffix == null || staffCodeSuffix === "") {
      this.user.staffCode = null;
    } else {
      this.user.staffCode = this.user.homeArea.code + staffCodeSuffix;
    }
  }

  get json() {
    return JSON.stringify(this.user);
  }
}
