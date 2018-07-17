import {Component, Input} from '@angular/core';
import {Transaction} from "../../model/transaction";

@Component({
  selector: 'roleselector',
  templateUrl: './roleselector.component.html'
})

export class RoleSelectorComponent{
  @Input()
  userTransactions: Transaction[];
  @Input()
  availableTransactions: Transaction[];
  @Input()
  componentWidth: string = "50%";
  @Input()
  componentHeight: string = "25%";
  @Input()
  buttonName: string = "Default";

  public optionsDisplayed: boolean;

  toggleOptions(){
    this.optionsDisplayed = !this.optionsDisplayed;
    if(this.optionsDisplayed){
      this.buttonName = "Hide Roles"
    }
    else{
      this.buttonName = "Show Roles";
    }
  }
}
