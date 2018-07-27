import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as moment from "moment";
import {now} from "moment";

@Component({
  selector: 'date',
  templateUrl: './date.component.html'
})
export class DateComponent implements OnInit {
  @Input() id: string;
  @Input() value: Date;
  @Input() required: boolean;
  @Input() readonly: boolean;
  @Input() min: Date;
  @Input() max: Date;
  @Output() valueChange: EventEmitter<Date> = new EventEmitter<Date>();

  date: number;
  month: number;
  year: number;

  ngOnInit(): void {
    if (this.value != null) {
      let m = moment(this.value);
      this.value = m.toDate();
      this.date = this.value.getDate();
      this.month = this.value.getMonth() + 1;
      this.year = this.value.getFullYear();
    }
  }

  change(): void {
    if (this.valid) {
      this.value = moment([this.year, this.month - 1, this.date]).toDate();
      this.valueChange.emit(this.value);
    }
  }

  setDaysFromToday(days: number): void {
    if (!this.readonly) {
      this.value = moment(now()).add(days, "days").toDate();
      this.ngOnInit();
      this.change();
    }
  }

  get valid(): boolean {
    if (this.year != null && this.month != null && this.date != null) {
      let m = moment([this.year, this.month - 1, this.date]);
      return m.isValid()
        && (this.max == null || m.isSameOrBefore(this.max))
        && (this.min == null || m.isSameOrAfter(this.min));
    } else return this.value == null;
  }
}
