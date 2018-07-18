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
  readOnly: boolean;

  public optionsDisplayed: boolean;

  toggleOptions(){
    this.optionsDisplayed = !this.optionsDisplayed;
  }

  userHasRole(transaction: Transaction){
    for(var i = 0; i < this.userTransactions.length; i++){
      if(this.userTransactions[i].name === transaction.name){
        return true;
      }
    }
    return false;
  }
}
