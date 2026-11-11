import { Pipe, PipeTransform, inject, LOCALE_ID } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'daytimeDisplay',
  standalone: true
})
export class DaytimeDisplayPipe implements PipeTransform {
  private locale = inject(LOCALE_ID, { optional: true }) || 'en-US';
  private datePipe = new DatePipe(this.locale);

  transform(value: Date | string | null | undefined): string | null {
    if (!value) return null;

    try {
      return this.datePipe.transform(value, 'dd.MM.yyyy HH:mm');
    } catch (error) {
      // Pokud DatePipe spadne (např. chybí cs-CZ lokalizační data), zachytíme to.
      // Vrátíme surový text, což zabrání zablokování HTML šablony.
      console.error('Chyba formátování v DaytimeDisplayPipe:', error);
      return String(value);
    }
  }
}
