import {Component, Input} from '@angular/core';

@Component({
  selector: 'item-selector',
  templateUrl: './item-selector.component.html'
})
export class ItemSelectorComponent {
  @Input() id: string;
  @Input() selected: any[];
  @Input() available: any[];
  @Input() labelMapper: Function;
  @Input() maxHeight: string = "auto";
  @Input() readonly: boolean;

  optionsDisplayed: boolean;
  filter: string = "";

  toggle(item): void {
    let index = this.selected.indexOf(item);
    if (index === -1) {
      this.selected.push(item);
    } else {
      this.selected.splice(index, 1);
    }
  }

  mapToLabel(item: any): string {
    return this.labelMapper(item);
  }

  isSelected(item: any): boolean {
    let label: string = this.mapToLabel(item);
    return this.selected.map(item => this.mapToLabel(item)).indexOf(label) !== -1;
  }

  get filtered(): any[] {
    if (this.filter.length === 0) return this.available;
    return this.available.filter(item => {
      return this.mapToLabel(item).toLowerCase().indexOf(this.filter.toLowerCase()) !== -1
    });
  }
}
