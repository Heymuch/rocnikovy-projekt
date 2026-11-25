import { Component, inject, input, ChangeDetectionStrategy } from '@angular/core';
import { CargoTypeService } from 'src/app/cargo/cargo-type-service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatError, MatFormField, MatLabel } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';

@Component({
  selector: 'app-cargo-type-select',
  imports: [
    MatLabel,
    MatSelect,
    MatFormField,
    MatError,
    MatOption,
    TranslatePipe,
    ValidationErrorPipe,
    ReactiveFormsModule,
  ],
  templateUrl: './cargo-type-select.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './cargo-type-select.scss',
})
export class CargoTypeSelect {
  private readonly cargoTypeService = inject(CargoTypeService);

  // Formulářová kontrolka předaná z nadřazeného formuláře
  readonly control = input.required<FormControl>(); // možná FormControl<number | null>

  // Načtení dat přímo do Angular Signalu (výchozí hodnota je prázdné pole)
  // V práci se to dělá trochu jinak - možná později předělat, ale tam je to asi zbytečně složité
  protected readonly cargoTypes = toSignal(this.cargoTypeService.getAll(), {
    initialValue: [],
  });
}
