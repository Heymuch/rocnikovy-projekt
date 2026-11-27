import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatError, MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ORDER_VALIDATION } from 'src/app/order/order.validation';
import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { CargoTypeSelect } from 'src/app/cargo/cargo-type-select/cargo-type-select';
import { StationCombobox } from 'src/app/station/station-combobox/station-combobox';

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
    TranslatePipe,
    CargoTypeSelect,
    StationCombobox,
  ],
  templateUrl: './new-order.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './new-order.scss',
})
export class NewOrder {
  readonly #fb = inject(FormBuilder);

  protected readonly maxLengths = {
    name: ORDER_VALIDATION.name_max_length,
  } as const;

  readonly formGroup = this.#fb.group({
    name: this.#fb.control<string | null>(null, [
      Validators.required,
      Validators.maxLength(this.maxLengths.name),
    ]),
    cargoTypeId: this.#fb.control<number | null>(null, [
      Validators.required
    ]),
    stationFrom: this.#fb.control<string | null>(null, [
      Validators.required
    ]),
    stationTo: this.#fb.control<string | null>(null, [
      Validators.required
    ]),
  });

  get name(): FormControl {
    return this.formGroup.controls['name'] as FormControl;
  }

  get cargoTypeId(): FormControl {
    return this.formGroup.controls['cargoTypeId'] as FormControl;
  }

  get stationFrom(): FormControl<string | null> {
    return this.formGroup.controls.stationFrom;
  }

  get stationTo(): FormControl<string | null> {
    return this.formGroup.controls.stationTo;
  }
}
