import {Component, Input} from '@angular/core';

@Component({
  selector: 'message',
  templateUrl: './message.component.html'
})
export class MessageComponent {
  @Input()
  message: string;
  @Input()
  severity = 'info';
}
