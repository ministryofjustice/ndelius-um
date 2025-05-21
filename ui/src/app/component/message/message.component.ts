import {Component, Input} from '@angular/core';

@Component({
    selector: 'message',
    templateUrl: './message.component.html',
    standalone: false
})
export class MessageComponent {
  @Input()
  message: string;
  @Input()
  severity = 'info';
}
