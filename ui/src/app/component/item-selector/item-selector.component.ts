import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator
} from '@angular/forms';

declare var $: any;
declare var Popper: any;

@Component({
  selector: 'item-selector',
  templateUrl: './item-selector.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
    }],

})

export class ItemSelectorComponent
  implements ControlValueAccessor, Validator, OnInit {
  @ViewChild('filterControl', {static: true}) filterControl: ElementRef;
  @ViewChild('toggleBtn', {static: true}) toggleBtn: ElementRef;
  @ViewChild('dropdown', {static: true}) dropdown: ElementRef;
  @Input() id: string;
  @Input() selected: any;
  @Input() maxHeight = 'auto';
  @Input() readonly: boolean;
  @Input() required = false;
  @Input() multiple: boolean;
  @Input() maxSelectable: number;
  @Input() alignRight: boolean;
  @Input() placeholder = 'Please select...';
  @Input() loadingText = 'Loading...';
  @Input() subMenuItems: {code: string, description: string}[];
  @Input() selectedSubMenuItem: string;
  @Input() disabled: boolean;
  @Output() selectedChange: EventEmitter<any> = new EventEmitter<any>();
  private availableItems: any[];

  readonly SELECTED_OPTION_SUB_MENU = '-1';
  dirty = false;
  filter = '';
  onlyShowSelected: boolean;
  subMenuMessage: string;

  @Input() idMapper: Function = null;
  @Input() getSubMenu: Function;
  @Input() labelMapper: Function = (item: any) => item;

  private propagateChange = (_: any) => {
  }
  private propagateTouchChange = (_: any) => {
  }

  toggle(item): void {
    if (this.multiple) {
      if (this.selected == null) {
        this.selected = [];
      }
      const index = this.selected.map(i => this.mapToId(i)).indexOf(this.mapToId(item));
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

  toggleAllItems() {
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

  toggleAllSubMenuItems() {
    const nonSelectedItems = this.available.filter(item => !this.selected.some(sel => sel.code === item.code));
    const currentSelectedItems = this.available.filter(item => this.selected.some(sel => sel.code === item.code));

    if (currentSelectedItems.length === this.available.length) {
      this.selected = this.selected.filter(
        item => !currentSelectedItems.some(
          currentSelectedItem => currentSelectedItem.code === item.code
        ));
    } else {
      this.selected = Array.of(...this.selected, ...nonSelectedItems);
      this.selected = Array.from(this.selected.reduce((output, item) => {
        if (!output.has(item.code)) {
          output.set(item.code, item);
        }
        return output;
      }, new Map()).values());
    }
    this.selectedChange.emit(this.selected);
    this.dirty = true;
  }

  focusOnFilter(): void {
    setTimeout(() => this.filterControl.nativeElement.focus(), 0);
  }

  mapToLabel(item: any): string {
    return this.labelMapper(item);
  }

  mapToId(item: any): string {
    return this.idMapper != null ? this.idMapper(item) : this.labelMapper(item);
  }

  isSelected(item: any): boolean {
    if (this.selected == null) {
      return false;
    }
    const id: string = this.mapToId(item);
    if (this.multiple) {
      return this.selected.map(i => this.mapToId(i)).indexOf(id) !== -1;
    } else {
      return this.mapToId(this.selected) === id;
    }
  }

  get available(): any[] {
    if (this.selected == null) {
      return this.availableItems || [];
    }
    if (this.availableItems == null) {
      return [];
    }

    const removeNullAndDuplicates = (el, pos, arr) => {
      return el != null && arr.map(item => this.mapToId(item)).indexOf(this.mapToId(el)) === pos;
    };

    if (this.subMenuItems != null) {
      return this.availableItems.filter(removeNullAndDuplicates);
    } else if (this.multiple) {
      return [...this.selected, ...this.availableItems].filter(removeNullAndDuplicates);
    } else {
      return [this.selected, ...this.availableItems].filter(removeNullAndDuplicates);
    }
  }

  @Input()
  set available(available: any[]) {
    this.availableItems = available;
  }

  get filtered(): any[] {
    let items;
    if (this.filter.length === 0) {
      items = this.available;
    } else {
      items = this.available.filter(item => {
        return this.mapToLabel(item).toLowerCase().indexOf(this.filter.toLowerCase()) !== -1;
      });
    }
    return items == null ? items : items.sort((a, b) => {
      const labelA = this.mapToLabel(a);
      const labelB = this.mapToLabel(b);
      return labelA === labelB ? 0 : labelA < labelB ? -1 : 1;
    });
  }

  get displayText(): string {
    if (this.available == null) {
      return this.loadingText;
    }

    if (this.multiple) {
      if (this.selected == null || this.selected.length === 0) {
        return this.placeholder;
      }
      if (this.selected.length === 1) {
        return this.mapToLabel(this.selected[0]);
      }
      return this.selected.length + ' selected';
    } else {
      if (this.selected == null) {
        return this.placeholder;
      }
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
    if (this.readonly) {
      return null;
    }
    if (this.required && (this.selected == null || (this.selected instanceof Array && this.selected.length === 0))) {
      return {'required': 'cannot be empty'};
    }
    if (this.multiple && this.maxSelectable != null && this.selected.length > this.maxSelectable) {
      return {'maxSelectable': this.maxSelectable, 'selected': this.selected.length};
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

  collapseDropdown(): boolean {
    if (this.dropdown.nativeElement.classList.contains('show')) {
      this.toggleBtn.nativeElement.click();
      this.toggleBtn.nativeElement.focus();
      return true;
    }
    return false;
  }

  getSubMenuList(): void {
    // Triggered when changing sub-menu, to populate the available items within the sub-menu
    if (this.selectedSubMenuItem === this.SELECTED_OPTION_SUB_MENU) {
      this.available = this.selected;
      this.onlyShowSelected = true;
    } else {
      this.available = null;
      this.onlyShowSelected = false;
      this.subMenuMessage = 'Loading...';
      this.getSubMenu(this.selectedSubMenuItem).subscribe(
        items => { this.subMenuMessage = ''; this.available = items; console.log('loaded items', this.available)},
        error => this.subMenuMessage = 'Error loading menu items'
      );
    }
  }

  disableComponent(): boolean {
    // If using a subMenu then the component is disabled when the subMenu has no items
    // Otherwise the item-selector is disabled when the available field is empty
    if (this.subMenuItems == null || this.subMenuItems.length === 0) {
      return this.available == null || this.available.length === 0 || this.disabled;
    } else {
      return this.subMenuItems.length === 0 || this.disabled;
    }
  }

  toggleAllCheckboxState(): boolean {
    if (this.selected != null && this.available != null) {
      if (this.subMenuItems == null || this.subMenuItems.length === 0) {
        return this.selected.length === this.available.length;
      } else {
        const currentSelectedItems = this.available.filter(item => this.selected.some(sel => sel.code === item.code));
        return currentSelectedItems.length > 0 && currentSelectedItems.length === this.available.length;
      }
    } else {
      return false;
    }
  }

  toggleCheckboxIndeterminateState(): boolean {
    if (this.selected != null && this.available != null) {
      if (this.subMenuItems == null || this.subMenuItems.length === 0) {
        return this.selected.length > 0 && this.selected.length !== this.available.length;
      } else {
        const currentSelectedItems = this.available.filter(item => this.selected.some(sel => sel.code === item.code));
        return currentSelectedItems.length > 0 && currentSelectedItems.length < this.available.length;
      }
    }
    return false;
  }
}
