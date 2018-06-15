import {Component} from "@angular/core";
import {User} from "../../model/User";

@Component({
  selector: 'user',
  templateUrl: './user.component.html'
})
export class UserComponent {
  user: User = new User();
}
