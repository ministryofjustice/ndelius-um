import {Component, Input} from '@angular/core';

@Component({
  selector: 'item-selector-single',
  templateUrl: './item-selector-single.component.html'
})
export class ItemSelectorSingleComponent {
  @Input() id: string;
  @Input() selected: any;
  @Input() available: any[];
  @Input() labelMapper: Function = (item: any) => item;
  @Input() maxHeight: string = "auto";
  @Input() readonly: boolean;

  optionsDisplayed: boolean;
  filter: string = "";

  toggle(item: any): void {
    this.selected = item;
  }

  mapToLabel(item: any): string {
    return this.labelMapper(item);
  }

  isSelected(item: any): boolean {
    return this.selected === item;
  }

  get filtered(): any[] {
    if (this.filter.length === 0) return this.available;
    return this.available.filter(item => {
      return this.mapToLabel(item).toLowerCase().indexOf(this.filter.toLowerCase()) !== -1
    });
  }
}
