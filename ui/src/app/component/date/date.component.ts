import {Component, forwardRef, Input} from '@angular/core';
import * as moment from "moment";
import {now} from "moment";
import {
  ControlValueAccessor,
  FormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';

@Component({
  selector: 'date',
  templateUrl: './date.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => DateComponent),
      multi: true,
    }]
})
export class DateComponent implements Validator, ControlValueAccessor {
  @Input() id: string;
  @Input() value: Date;
  @Input() required: boolean;
  @Input() readonly: boolean;
  @Input() min: Date;
  @Input() max: Date;

  date: number;
  month: number;
  year: number;

  change(): void {

    if(this.year == null || this.month == null || this.date == null){
      this.writeValue(null);
    } else {
      let m = moment([this.year, this.month - 1, this.date]);
      if (m.isValid()) {
        this.writeValue(moment([this.year, this.month - 1, this.date]).toDate());
      } else this.writeValue(null);
    }
    this.propagateChange(this.value);
  }

  setDaysFromToday(days: number): boolean {
    if (!this.readonly) {
      this.writeValue(moment(now()).add(days, "days").toDate());
      this.change();
    }
    return false;
  }

  private propagateChange = (_: any) => { };
  private propagateTouchChange = (_: any) => { };

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.propagateTouchChange = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.readonly = isDisabled;
  }

  public writeValue(obj: any) {
    this.value = obj;
    if (this.value != null) {
      let m = moment(this.value);
      this.value = m.toDate();
      this.date = this.value.getDate();
      this.month = this.value.getMonth() + 1;
      this.year = this.value.getFullYear();
    }
  }

  public validate(c: FormControl): ValidationErrors {
    if(this.year == null && this.month == null && this.date == null){
      return this.required ? {"required": "date is required"} : null;
    }
    if(this.year == null || this.month == null || this.date == null){
      return {"incomplete" : "All date fields are required"}
    }
    let m = moment([this.year, this.month - 1, this.date]);
    if (!m.isValid())return {"invalidDate": "date is invalid"};
    if (this.max != null && m.isAfter(this.max)) return {"max": "should be before " + this.max};
    if (this.min != null && m.isBefore(this.min)) return {"min": "should be after " + this.min};
    return null;
  }
}
