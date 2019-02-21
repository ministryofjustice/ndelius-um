import {Component, ElementRef, EventEmitter, forwardRef, Input, OnInit, Output, ViewChild} from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';

declare var $: any;
declare var Popper: any;

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
    implements ControlValueAccessor, Validator, OnInit
{
  @ViewChild("filterControl") filterControl: ElementRef;
  @ViewChild("toggleBtn") toggleBtn: ElementRef;
  @Input() id: string;
  @Input() selected: any;
  @Input() available: any[];
  @Input() labelMapper: Function = (item: any) => item;
  @Input() idMapper: Function = null;
  @Input() maxHeight: string = "auto";
  @Input() readonly: boolean;
  @Input() required: boolean = false;
  @Input() multiple: boolean;
  @Input() alignRight: boolean;
  @Output() selectedChange: EventEmitter<any> = new EventEmitter<any>();

  dirty: boolean = false;
  filter: string = "";
  onlyShowSelected: boolean;

  private propagateChange = (_: any) => { };
  private propagateTouchChange = (_: any) => { };

  toggle(item): void {
    if (this.multiple) {
      if (this.selected == null) this.selected = [];
      let index = this.selected.map(item => this.mapToId(item)).indexOf(this.mapToId(item));
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

  toggleAllItems(){
    if (this.selected == null) {
      this.selected = [];
    }
    if (this.selected.length < this.available.length) {
      this.selected = Array.of(...this.available);
    } else {
      this.selected = [];
    }
    this.selectedChange.emit(this.selected);
    this.propagateChange(this.selected);
    this.dirty = true;
  }

  focusOnFilter(): void {
    setTimeout(() => this.filterControl.nativeElement.focus(), 0);
  }

  mapToLabel(item: any): string {
    return this.labelMapper(item);
  }

  mapToId(item: any): string {
    return this.idMapper != null? this.idMapper(item): this.labelMapper(item);
  }

  isSelected(item: any): boolean {
    if (this.selected == null) return false;
    let id: string = this.mapToId(item);
    if (this.multiple) {
      return this.selected.map(item => this.mapToId(item)).indexOf(id) !== -1;
    } else {
      return this.mapToId(this.selected) === id;
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
    return items == null? items: items.sort((a, b) => {
      let labelA = this.mapToLabel(a);
      let labelB = this.mapToLabel(b);
      return labelA == labelB? 0: labelA < labelB? -1: 1;
    });
  }

  get displayText(): string {
    if (this.available == null) return "Loading...";

    if (this.multiple) {
      if (this.selected == null || this.selected.length == 0) return "Please select...";
      if (this.selected.length == 1) return this.mapToLabel(this.selected[0]);
      return this.selected.length + " selected";
    } else {
      if (this.selected == null) return "Please select...";
      return this.mapToLabel(this.selected);
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.propagateTouchChange = fn;
  }

  writeValue(obj: any) {
    if (obj) {
      this.selected = obj;
    }
  }

  validate(c: FormControl): ValidationErrors {
    if(this.required == false || this.readonly) return null;
    if(this.selected == null || (this.selected instanceof Array && this.selected.length == 0)) {
      return {"list": "cannot be empty"}
    }
    return null;
  }

  setDisabledState(isDisabled: boolean): void {
    this.readonly = isDisabled;
  }

  updateDropDown() {
    $(this.toggleBtn.nativeElement).dropdown('update');
  }

  ngOnInit(): void {
    Popper.Defaults.modifiers.computeStyle.gpuAcceleration = false;
  }
}
