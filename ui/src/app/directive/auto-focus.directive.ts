import {AfterViewInit, Directive, ElementRef} from '@angular/core';

@Directive({
    selector: '[autoFocus]',
    // eslint-disable-next-line
    standalone: false
})
export class AutoFocusDirective implements AfterViewInit {
  constructor(private el: ElementRef) {}
  ngAfterViewInit() {
    this.el.nativeElement.focus();
  }
}
