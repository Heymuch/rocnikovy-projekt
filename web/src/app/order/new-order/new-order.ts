import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatError, MatFormField, MatHint, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ORDER_VALIDATION } from 'src/app/order/order.validation';
import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { CargoTypeSelect } from 'src/app/cargo/cargo-type-select/cargo-type-select';
import { StationCombobox } from 'src/app/station/station-combobox/station-combobox';
import { MatDatepicker, MatDatepickerInput, MatDatepickerToggle } from '@angular/material/datepicker';
import { MatTimepicker, MatTimepickerInput, MatTimepickerToggle } from '@angular/material/timepicker';
import {
  arrivalAfterDepartureValidator,
  ArrivalErrorStateMatcher, differentStationsValidator,
  getMinDepartureDate,
  minDepartureDateValidator
} from 'src/app/order/order.validators';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { NewOrderStore } from 'src/app/order/new-order/new-order.store';
import { Router } from '@angular/router';
import { OrderCalculateRequestDTO, OrderCreateRequestDTO } from 'src/app/order/order';
import { RouteMap } from 'src/app/commons/route-map/route-map';
import { DecimalPipe } from '@angular/common';

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
    StationCombobox,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    MatTimepickerInput,
    MatTimepickerToggle,
    MatTimepicker,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    RouteMap,
    DecimalPipe
  ],
  providers: [NewOrderStore],
  templateUrl: './new-order.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './new-order.scss',
})
export class NewOrder {
  readonly #fb = inject(FormBuilder);
  readonly #router = inject(Router);

  readonly store = inject(NewOrderStore);

  protected readonly validation = ORDER_VALIDATION;
  protected readonly minDate = getMinDepartureDate();
  protected readonly arrivalErrorMatcher = new ArrivalErrorStateMatcher();

  readonly formGroup = this.#fb.group({
    name: this.#fb.control<string | null>(null, [
      Validators.required,
      Validators.maxLength(this.validation.name.maxLength),
    ]),
    preferredDeparture: this.#fb.control<Date | null>(null, [
      Validators.required,
      minDepartureDateValidator(),
    ]),
    preferredArrival: this.#fb.control<Date | null>(null, [
      Validators.required,
      minDepartureDateValidator(),
    ]),
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
      stationFrom: this.#fb.control<string | null>(null, [
        Validators.required
      ]),
      stationTo: this.#fb.control<string | null>(null, [
        Validators.required
      ]),
  },
    { validators: [arrivalAfterDepartureValidator, differentStationsValidator] },
    );

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

  get stationFrom(): FormControl<string | null> {
    return this.formGroup.controls.stationFrom;
  }

  get stationTo(): FormControl<string | null> {
    return this.formGroup.controls.stationTo;
  }

  protected get minArrivalDate(): Date {
    const departure = this.preferredDeparture.value;
    if (departure instanceof Date && departure.getTime() > this.minDate.getTime()) {
      return departure;
    }
    return this.minDate;
  }

  protected cancel(): void {
    this.#router.navigate(['/my-orders']);
  }

  protected calculate(): void {
    if (this.formGroup.invalid || this.store.isCalculating()) {
      this.formGroup.markAllAsTouched();
      return;
    }

    const payload: OrderCalculateRequestDTO = {
      stationFrom: this.stationFrom.value!,
      stationTo: this.stationTo.value!,
      cargoTypeId: this.cargoTypeId.value!,
      volume: this.volume.value!,
      weight: this.weight.value!,
    };

    this.store.calculateOrder(payload);
  }

  protected submitOrder(): void {
    if (this.formGroup.invalid || this.store.isSubmitting()) {
      return;
    }

    const payload: OrderCreateRequestDTO = {
      name: this.name.value!.trim(),
      preferredDeparture: this.preferredDeparture.value!.toISOString(),
      preferredArrival: this.preferredArrival.value!.toISOString(),
      route: {
        stationFrom: this.stationFrom.value!,
        stationTo: this.stationTo.value!,
      },
      cargo: {
        cargoTypeId: this.cargoTypeId.value!,
        volume: this.volume.value!,
        weight: this.weight.value!,
      },
    };

    this.store.createOrder(payload);
  }


}
