import { Component, inject } from '@angular/core';
import { MatError, MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ORDER_VALIDATION } from 'src/app/order/order.validation';
import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-new-order',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatHint,
    MatError,
    ValidationErrorPipe,
    TranslatePipe
  ],
  templateUrl: './new-order.html',
  styleUrl: './new-order.scss',
})
export class NewOrder {
  readonly #fb = inject(FormBuilder)

  protected readonly maxLengths = {
    name: ORDER_VALIDATION.name_max_length,
  } as const



  readonly formGroup = this.#fb.group(
    {
       name: this.#fb.control<string | null>(null, [
        Validators.required,
        Validators.maxLength(this.maxLengths.name),
      ]),
    },
  )

  get name(): FormControl {
    return this.formGroup.controls['name'] as FormControl
  }
}
