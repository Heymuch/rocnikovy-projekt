import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatError, MatFormField, MatHint, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ORDER_VALIDATION } from 'src/app/order/order.validation';
import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { CargoTypeSelect } from 'src/app/cargo/cargo-type-select/cargo-type-select';
import { MatDatepicker, MatDatepickerInput, MatDatepickerToggle } from '@angular/material/datepicker';
import { MatTimepicker, MatTimepickerInput, MatTimepickerToggle } from '@angular/material/timepicker';

@Component({
  selector: 'app-new-order',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatHint,
    MatError,
    MatSuffix,
    ValidationErrorPipe,
    TranslatePipe,
    CargoTypeSelect,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    MatTimepickerInput,
    MatTimepickerToggle,
    MatTimepicker
  ],
  templateUrl: './new-order.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './new-order.scss',
})
export class NewOrder {
  readonly #fb = inject(FormBuilder);

  protected readonly validation = ORDER_VALIDATION;

  readonly formGroup = this.#fb.group({
    name: this.#fb.control<string | null>(null, [
      Validators.required,
      Validators.maxLength(this.validation.name.maxLength),
    ]),
    preferredDeparture: this.#fb.control<Date | null>(null, [Validators.required]),
    preferredArrival: this.#fb.control<Date | null>(null, [Validators.required]),
    cargo: this.#fb.group({
      cargoTypeId: this.#fb.control<number | null>(null, [Validators.required]),
      volume: this.#fb.control<number | null>(null, [
        Validators.required,
        Validators.min(this.validation.cargo.volume.min),
        Validators.max(this.validation.cargo.volume.max),
      ]),
      weight: this.#fb.control<number | null>(null, [
        Validators.required,
        Validators.min(this.validation.cargo.weight.min),
        Validators.max(this.validation.cargo.weight.max),
      ]),
    }),
  });

  get name(): FormControl<string | null> {
    return this.formGroup.controls.name;
  }

  get preferredDeparture(): FormControl<Date | null> {
    return this.formGroup.controls.preferredDeparture;
  }

  get preferredArrival(): FormControl<Date | null> {
    return this.formGroup.controls.preferredArrival;
  }

  get cargoTypeId(): FormControl<number | null> {
    return this.formGroup.controls.cargo.controls.cargoTypeId;
  }

  get volume(): FormControl<number | null> {
    return this.formGroup.controls.cargo.controls.volume;
  }

  get weight(): FormControl<number | null> {
    return this.formGroup.controls.cargo.controls.weight;
  }
}
