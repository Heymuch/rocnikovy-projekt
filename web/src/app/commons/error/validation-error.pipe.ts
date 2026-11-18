import { Pipe, PipeTransform } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

@Pipe({
  name: 'validationError',
  standalone: true
})
export class ValidationErrorPipe implements PipeTransform {
  // private readonly translateService = inject(TranslateService)
  transform(errors: ValidationErrors | null | undefined): string | null{
    if (!errors) return '';

    // Zde mapujeme typ chyby na klíče do tvých cs.json / en.json
    if (errors['required']) {
      return 'validation.required'
      // return this.translateService.instant('validation.required')
    }
    if (errors['max']) {
      // errors['maxlength'] obsahuje např. { requiredLength: 50, actualLength: 55 }
      return 'validation.maxLength' //todo: možná nebude potřeba
    //   v práci:
    //   const max = errors['max'].max
    //   return this.translateService.instant('validation.max', {max})
    }

    return 'validation.invalid'
  }
}
