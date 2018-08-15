import {Component, EventEmitter, forwardRef, Input, Output} from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';

@Component({
  selector: 'item-selector',
  templateUrl: './item-selector.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ItemSelectorComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ItemSelectorComponent),
      multi: true,
    }]
})
export class ItemSelectorComponent
    implements ControlValueAccessor, Validator
{
  @Input() id: string;
  @Input() selected: any;
  @Input() available: any[];
  @Input() labelMapper: Function = (item: any) => item;
  @Input() maxHeight: string = "auto";
  @Input() readonly: boolean;
  @Input() required: boolean;
  @Input() multiple: boolean;
  @Output() selectedChange: EventEmitter<any> = new EventEmitter<any>();

  dirty: boolean = false;
  optionsDisplayed: boolean;
  filter: string = "";

  toggle(item): void {
    if (this.multiple) {
      if (this.selected == null) this.selected = [];
      let index = this.selected.map(item => this.mapToLabel(item)).indexOf(this.mapToLabel(item));
      if (index === -1) {
        this.selected.push(item);
      } else {
        this.selected.splice(index, 1);
      }
    } else {
      this.selected = item;
    }
    this.selectedChange.emit(this.selected);
    this.propagateChange(this.selected);
    this.dirty = true;
  }

  toggleOptionsDisplayed() {
    if ((this.available == null || this.available.length === 0) && !this.optionsDisplayed) return;
    this.optionsDisplayed = !this.optionsDisplayed;
    this.propagateTouchChange(this.selected);
  }

  hideOptionsOnSingleSelect(item): boolean {
    if (!this.multiple && !this.isSelected(item)) this.optionsDisplayed = false;
    return true;
  }

  mapToLabel(item: any): string {
    return this.labelMapper(item);
  }

  isSelected(item: any): boolean {
    if (this.selected == null) return false;
    let label: string = this.mapToLabel(item);
    if (this.multiple) {
      return this.selected.map(item => this.mapToLabel(item)).indexOf(label) !== -1;
    } else {
      return this.mapToLabel(this.selected) === label;
    }
  }

  get filtered(): any[] {
    let items;
    if (this.filter.length === 0) {
      items = this.available;
    } else {
      items = this.available.filter(item => {
        return this.mapToLabel(item).toLowerCase().indexOf(this.filter.toLowerCase()) !== -1
      });
    }
    return items.sort((a, b) => {
      let labelA = this.mapToLabel(a);
      let labelB = this.mapToLabel(b);
      return labelA == labelB? 0: labelA < labelB? -1: 1;
    });
  }

  private propagateChange = (_: any) => { };
  private propagateTouchChange = (_: any) => { };

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.propagateTouchChange = fn;
  }

  public writeValue(obj: any) {
    if (obj) {
      this.selected = obj;
    }
  }

  public validate(c: FormControl): ValidationErrors {
    if(this.required == false || this.readonly) return null;
    if(this.selected == null || (this.selected instanceof Array && this.selected.length == 0)) {
      return {"list": "cannot be empty"}
    }
    return null;
  }

  setDisabledState(isDisabled: boolean): void {
    this.readonly = isDisabled;
  }
}
