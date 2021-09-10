import {Component, OnInit, ViewChild} from '@angular/core';
import {User} from '../../model/user';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {debounceTime, distinctUntilChanged, filter, flatMap, map} from 'rxjs/operators';
import {of} from 'rxjs';
import {UserService} from '../../service/user.service';
import {AuthorisationService} from '../../service/impl/authorisation.service';
import {RoleService} from '../../service/role.service';
import {Role} from '../../model/role';
import {Dataset} from '../../model/dataset';
import {DatasetService} from '../../service/dataset.service';
import {Team} from '../../model/team';
import {TeamService} from '../../service/team.service';
import {RoleGroup} from '../../model/role-group';
import {OrganisationService} from '../../service/organisation.service';
import {StaffGrade} from '../../model/staff-grade';
import {StaffGradeService} from '../../service/staff-grade.service';
import {AppComponent} from '../app/app.component';
import {NgForm, NgModel} from '@angular/forms';
import {RecentUsersUtils} from '../../util/recent-users.utils';
import {GroupService} from '../../service/group.service';
import {LabelMappingUtils} from '../../util/label-mapping.utils';
import {Groups} from '../../model/groups';
import {UserHistoryItem} from '../../model/user-history-item';
import {HistoryService} from '../../service/history.service';
import {UserConstants} from './user.constants';

@Component({
  selector: 'user',
  templateUrl: './user.component.html',
})

export class UserComponent implements OnInit {
  loaded: boolean;
  saving: boolean;
  @ViewChild('form') form: NgForm;
  @ViewChild('rolesControl') rolesControl: NgModel;
  @ViewChild('staffCode') staffCodeControl: NgModel;
  mode: string;
  params: Params;
  user: User;
  existingHomeAreaCode: string;
  teams: Team[];
  datasets: Dataset[];
  establishments: Dataset[];
  roles: Role[];
  roleGroups: RoleGroup[];
  selectedRoleGroups: RoleGroup[];
  groups: Groups;
  staffGrades: StaffGrade[];
  subContractedProviders: Dataset[];
  history: UserHistoryItem[];
  userWithStaffCode: User;
  loadingStaffCode: boolean;
  generatingStaffCode: boolean;
  globalMinDate: Date = new Date(1900, 0, 1);
  globalMaxDate: Date = new Date(2099, 11, 31);
  systemUserNames: string[] = UserConstants.SYSTEM_USER_NAMES;

  LabelMappingUtils = LabelMappingUtils;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private roleService: RoleService,
    private groupService: GroupService,
    private datasetService: DatasetService,
    private organisationService: OrganisationService,
    private staffGradeService: StaffGradeService,
    private historyService: HistoryService,
    public auth: AuthorisationService,
    public teamService: TeamService) {
  }

  ngOnInit(): void {
    this.route.params
      .pipe(flatMap(params => {
        this.params = params;
        if (params.id != null) {
          RecentUsersUtils.add(params.id);
          this.mode = this.auth.canUpdateUser() ? 'Update' : 'View';
          this.historyService.getHistory(params.id).subscribe(history => this.history = history);
          return this.userService.read(params.id);
        } else {
          return this.route.queryParams.pipe(flatMap(query => {
            if (query.copy != null) {
              this.mode = 'Add';
              return this.userService.read(query.copy).pipe(map(user => {
                // Clear out the details we don't want to copy to the new user
                user.username = user.staffCode = user.staffGrade = user.teams = user.subContractedProvider
                  = user.created = user.updated = null;
                return user;
              }));
            } else {
              this.mode = 'Add';
              return of(new User());
            }
          }));
        }
      }))
      .subscribe((user: User) => {
        this.user = user;
        this.addSelectableRoles(user.roles || []);
        this.loaded = true;
        this.existingHomeAreaCode = user.homeArea?.code;
        this.homeAreaChanged();
        setTimeout(() => {
          this.staffCodeControl.valueChanges
            .pipe(debounceTime(500), distinctUntilChanged(), filter(val => val != null))
            .subscribe(() => this.staffCodeChanged());
          this.user.roles = [...this.user.roles];
        });
      });


    this.roleService.groups().subscribe((roleGroups: RoleGroup[]) => {
      this.roleGroups = roleGroups;
    });

    this.roleService.roles().subscribe(roles => this.addSelectableRoles(roles));

    this.groupService.groups().subscribe(groups => this.groups = groups);

    this.datasetService.datasets().subscribe((datasets: Dataset[]) => {
      this.datasets = datasets;
      if (this.user != null && this.user.datasets == null) {
        this.user.datasets = [];
      }
    });

    this.datasetService.establishments().subscribe((establishments: Dataset[]) => {
      this.establishments = establishments;
      if (this.user != null && this.user.establishments == null) {
        this.user.establishments = [];
      }
    });

    this.staffGradeService.staffGrades().subscribe((staffGrades: StaffGrade[]) => {
      this.staffGrades = staffGrades;
    });
  }


  private addSelectableRoles(roles: Role[]) {
    this.roles = this.roles || [];
    this.roles = [...this.roles, ...roles.filter(role => this.roles.map(r => r.name).indexOf(role.name) === -1)];
  }

  applyRoleGroup(): void {
    if (this.selectedRoleGroups != null) {
      if (this.user.roles == null) {
        this.user.roles = [];
      }
      this.selectedRoleGroups.forEach(selectedRoleGroup => {
        this.roleService.group(selectedRoleGroup.name).subscribe(group => {
          const userRoleNames = this.user.roles.map(r => r.name);
          this.user.roles = [...this.user.roles, ...group.roles.filter(role => userRoleNames.indexOf(role.name) === -1)];
          this.rolesControl.control.markAsDirty();
        });
      });
    }
  }

  homeAreaChanged() {
    if (this.user.homeArea != null) {
      this.subContractedProviders = null;
      this.datasetService.subContractedProviders(this.user.homeArea.code).subscribe((subContractedProviders: Dataset[]) => {
        this.subContractedProviders = subContractedProviders;
      });
    } else {
      this.subContractedProviders = [];
    }
  }

  staffCodeChanged(): void {
    if (!this.user.staffCode) {
      this.user.teams = null;
    }
    this.userWithStaffCode = null;
    this.loadingStaffCode = true;
    this.userService.readByStaffCode(this.user.staffCode)
      .subscribe(user => {
          this.userWithStaffCode = user;
          this.user.staffGrade = user.staffGrade;
          this.user.teams = user.teams;
          this.user.subContractedProvider = user.subContractedProvider;
          this.loadingStaffCode = false;
        },
        () => this.loadingStaffCode = false);
  }

  datasetsChanged() {
    if (this.user.homeArea != null && this.user.datasets.map(d => d.code).indexOf(this.user.homeArea.code) === -1) {
      this.user.homeArea = null;
      this.homeAreaChanged();
    }
    if (this.user.datasets.length === 1 && this.user.homeArea == null) {
      this.user.homeArea = this.user.datasets[0];
      this.homeAreaChanged();
    }
  }

  submit(): void {
    if (!this.form.valid) {
      Object.keys(this.form.controls).forEach(key => {
        const abstractControl = this.form.controls[key];
        abstractControl.markAsDirty();
        abstractControl.updateValueAndValidity();
      });
      AppComponent.error('Please correct any highlighted fields before submitting user details.');
      window.scrollTo(0, 0);
      return;
    }
    if (this.mode === 'Add') {
      this.saving = true;
      window.scrollTo(0, 0);
      this.userService.create(this.user).subscribe(() => {
        this.router.navigate(['/user/' + this.user.username]).then(() => {
          AppComponent.success('Created ' + this.user.username + ' successfully.');
          this.saving = false;
        });
      }, () => this.saving = false);
    } else if (this.mode === 'Update') {
      this.saving = true;
      window.scrollTo(0, 0);
      this.userService.update(this.params.id, this.user).subscribe(() => {
        if (this.params.id !== this.user.username) {
          RecentUsersUtils.remove(this.params.id);
        }
        this.router.navigate(['/user/' + this.user.username], {replaceUrl: true}).then(() => {
          AppComponent.success('Updated ' + this.user.username + ' successfully.');
          this.saving = false;
        });
      }, () => this.saving = false);
    } else {
      console.error('Unsupported mode', this.mode);
    }
  }

  generateStaffCode(): void {
    this.generatingStaffCode = true;
    this.datasetService.nextStaffCode(this.user.homeArea.code)
      .subscribe(staffCode => {
          this.user.staffCode = staffCode;
          this.staffCodeChanged();
          this.generatingStaffCode = false;
        },
        () => this.generatingStaffCode = false);
  }

  backButtonAlert(): void {
    if (!this.form.dirty ||
      confirm('Any changes made on this screen will be lost. Select OK to continue or Cancel to stay on this screen.')) {
      window.history.back();
    }
  }

  isSystemUser(username: string): boolean {
    return this.systemUserNames.some(v => username.toUpperCase().includes(v));
  }
}
