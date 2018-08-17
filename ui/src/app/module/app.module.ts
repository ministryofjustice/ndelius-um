import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NgModule} from '@angular/core';

import {AppComponent} from '../component/app/app.component';
import {SearchComponent} from "../component/search/search.component";
import {UserComponent} from "../component/user/user.component";
import {AppRoutingModule} from "./app.routing.module";
import {LoginInterceptor} from "../interceptor/login.interceptor";
import {environment} from "../../environments/environment";
import {RestUserService} from "../service/impl/rest.user.service";
import {UserService} from "../service/user.service";
import {ErrorInterceptor} from "../interceptor/error.interceptor";
import {MessageComponent} from "../component/message/message.component";
import {ItemSelectorComponent} from "../component/item-selector/item-selector.component";
import {RestRoleService} from "../service/impl/rest.role.service";
import {RoleService} from "../service/role.service";
import {DatasetService} from "../service/dataset.service";
import {RestDatasetService} from "../service/impl/rest.dataset.service";
import {RestTeamService} from "../service/impl/rest.team.service";
import {TeamService} from "../service/team.service";
import {DateComponent} from "../component/date/date.component";
import {OrganisationService} from "../service/organisation.service";
import {RestOrganisationService} from "../service/impl/rest.organisation.service";
import {StaffGradeService} from "../service/staff-grade.service";
import {RestStaffGradeService} from "../service/impl/rest.staff-grade.service";
import {UserMigrationComponent} from "../component/user-migration/user-migration.component";
import {PapaParseModule} from "ngx-papaparse";
import {AliasService} from "../service/alias.service";
import {RestAliasService} from "../service/impl/rest.alias.service";

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    UserComponent,
    UserMigrationComponent,
    MessageComponent,
    ItemSelectorComponent,
    DateComponent],
  imports: [BrowserModule, FormsModule, HttpClientModule, PapaParseModule, AppRoutingModule],
  providers: [
    {provide: UserService, useClass: RestUserService},
    {provide: RoleService, useClass: RestRoleService},
    {provide: DatasetService, useClass: RestDatasetService},
    {provide: TeamService, useClass: RestTeamService},
    {provide: OrganisationService, useClass: RestOrganisationService},
    {provide: StaffGradeService, useClass: RestStaffGradeService},
    {provide: AliasService, useClass: RestAliasService},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    ...(environment.production? []: [{provide: HTTP_INTERCEPTORS, useClass: LoginInterceptor, multi: true}])
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
