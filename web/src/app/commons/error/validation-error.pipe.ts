import { Pipe, PipeTransform } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

export type ValidationErrorResult = {
  key: string;
  params?: Record<string, unknown>;
};

@Pipe({
  name: 'validationError',
})
export class ValidationErrorPipe implements PipeTransform {
  transform(errors: ValidationErrors | null | undefined): ValidationErrorResult | null {
    if (!errors) {
      return null;
    }

    if (errors['required']) {
      return { key: 'validation.required' };
    }

    if (errors['maxlength']) {
      return {
        key: 'validation.maxLength',
        params: { max: errors['maxlength'].requiredLength },
      };
    }

    if (errors['min']) {
      return {
        key: 'validation.min',
        params: { min: errors['min'].min },
      };
    }

    if (errors['max']) {
      return {
        key: 'validation.max',
        params: { max: errors['max'].max },
      };
    }

    if (errors['matDatepickerParse']) {
      return { key: 'validation.invalidDate' };
    }

    if (errors['matTimepickerParse']) {
      return { key: 'validation.invalidTime' };
    }

    if (errors['minDepartureDate']) {
      return {
        key: 'validation.minDepartureDate',
        params: { days: errors['minDepartureDate'].days },
      };
    }

    if (errors['arrivalBeforeDeparture']) {
      return { key: 'validation.arrivalBeforeDeparture' };
    }

    if (errors['matDatepickerMin']) {
      return { key: 'validation.dateTooEarly' };
    }

    return { key: 'validation.invalid' };
  }
}
