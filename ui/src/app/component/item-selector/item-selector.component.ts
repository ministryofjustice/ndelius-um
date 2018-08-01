import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'item-selector',
  templateUrl: './item-selector.component.html'
})
export class ItemSelectorComponent {
  @Input() id: string;
  @Input() selected: any;
  @Input() available: any[];
  @Input() labelMapper: Function = (item: any) => item;
  @Input() maxHeight: string = "auto";
  @Input() readonly: boolean;
  @Input() multiple: boolean;
  @Output() selectedChange: EventEmitter<any> = new EventEmitter<any>();

  optionsDisplayed: boolean;
  filter: string = "";

  toggle(item): void {
    if (this.multiple) {
      if (this.selected == null) this.selected = [];
      let index = this.selected.indexOf(item);
      if (index === -1) {
        this.selected.push(item);
      } else {
        this.selected.splice(index, 1);
      }
    } else {
      this.selected = item;
      this.optionsDisplayed = false;
    }
    this.selectedChange.emit(this.selected);
  }

  toggleOptionsDisplayed() {
    if ((this.available == null || this.available.length === 0) && !this.optionsDisplayed) return;
    this.optionsDisplayed = !this.optionsDisplayed
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
    if (this.filter.length === 0) return this.available;
    return this.available.filter(item => {
      return this.mapToLabel(item).toLowerCase().indexOf(this.filter.toLowerCase()) !== -1
    });
  }
}
