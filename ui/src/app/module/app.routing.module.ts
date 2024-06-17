import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {SearchComponent} from '../component/search/search.component';
import {UserComponent} from '../component/user/user.component';

// Note: If you're adding a route, don't forget to also add it to the back-end UIController!
const routes: Routes = [
  {path: '', redirectTo: '/search', pathMatch: 'full'},
  {path: 'search', component: SearchComponent},
  {path: 'search?q=:query', component: SearchComponent},
  {path: 'user', component: UserComponent},
  {path: 'user?copy=:copy', component: UserComponent},
  {path: 'user/:id', component: UserComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
