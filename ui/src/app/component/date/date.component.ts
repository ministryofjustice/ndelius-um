import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as moment from "moment";

@Component({
  selector: 'date',
  templateUrl: './date.component.html'
})
export class DateComponent implements OnInit {
  @Input() value: Date;
  @Input() required: boolean;
  @Input() readonly: boolean;
  @Output() valueChange: EventEmitter<Date> = new EventEmitter<Date>();

  date: number;
  month: number;
  year: number;
  valid: boolean = !this.required;

  ngOnInit(): void {
    if (this.value != null) {
      let m = moment(this.value);
      this.value = m.toDate();
      this.date = this.value.getDate();
      this.month = this.value.getMonth() + 1;
      this.year = this.value.getFullYear();
      this.valid = m.isValid();
    }
  }

  change(): void {
    if (this.year != null && this.month != null && this.date != null) {
      let m = moment([this.year, this.month - 1, this.date]);
      this.valid = m.isValid();
      if (this.valid) {
        this.value = m.toDate();
        this.valueChange.emit(this.value);
      }
    } else if (this.value != null) {
      this.valid = false;
    }
  }
}
