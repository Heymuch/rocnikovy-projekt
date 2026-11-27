import {
  AbstractControl,
  FormGroupDirective,
  NgForm,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { ORDER_VALIDATION } from './order.validation';

/**
 * Vrátí nejbližší povolené datum odjezdu (půlnoc za N dní od dneška).
 */
export function getMinDepartureDate(
  daysOffset: number = ORDER_VALIDATION.dates.minDaysBeforeDeparture,
): Date {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  date.setDate(date.getDate() + daysOffset);
  return date;
}

/**
 * Kontroluje, že zadaný termín je alespoň N dní v budoucnosti.
 */
export function minDepartureDateValidator(
  daysOffset: number = ORDER_VALIDATION.dates.minDaysBeforeDeparture,
): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!(value instanceof Date) || Number.isNaN(value.getTime())) {
      return null;
    }

    const minDate = getMinDepartureDate(daysOffset);
    return value.getTime() < minDate.getTime()
      ? { minDepartureDate: { days: daysOffset } }
      : null;
  };
}

/**
 * Křížový validátor na úrovni FormGroup: příjezd musí být až po odjezdu.
 */
export const arrivalAfterDepartureValidator: ValidatorFn = (
  group: AbstractControl,
): ValidationErrors | null => {
  const departure = group.get('preferredDeparture')?.value;
  const arrival = group.get('preferredArrival')?.value;

  if (!(departure instanceof Date) || !(arrival instanceof Date)) {
    return null;
  }

  return arrival.getTime() <= departure.getTime() ? { arrivalBeforeDeparture: true } : null;
};

/**
 * Zobrazí chybový stav na poli příjezdu i v případě křížové chyby na FormGroup.
 */
export class ArrivalErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = !!form?.submitted;
    const isInteracted = (control?.dirty || control?.touched || isSubmitted);
    const hasControlError = !!(control?.invalid && isInteracted);
    const hasGroupError = !!(
      control?.parent?.hasError('arrivalBeforeDeparture') && isInteracted
    );
    return hasControlError || hasGroupError;
  }
}

/**
 * Křížový validátor na úrovni FormGroup: výchozí a cílová stanice nesmí být stejné.
 */
export const differentStationsValidator: ValidatorFn = (
  group: AbstractControl,
): ValidationErrors | null => {
  const from = group.get('stationFrom')?.value;
  const to = group.get('stationTo')?.value;

  if (!from || !to) {
    return null;
  }

  return from === to ? { sameStations: true } : null;
};
