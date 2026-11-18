
import {Pipe, PipeTransform, inject, LOCALE_ID} from '@angular/core';
import {DatePipe} from '@angular/common';

@Pipe({
  name: 'daytimeDisplay',
  standalone: true
})
export class DaytimeDisplayPipe implements PipeTransform {
  private readonly locale = inject(LOCALE_ID);
  private readonly datePipe = new DatePipe(this.locale);

  transform(value: Date | string | null | undefined): string | null {
    if (value == null || value === '') {
      return null;
    }

    return this.datePipe.transform(value, 'dd.MM.yyyy HH:mm');
  }
}
