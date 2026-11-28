import {
  Component,
  inject,
  input,
  signal,
  ChangeDetectionStrategy, model
} from '@angular/core';

import { FormControl, Validators } from '@angular/forms';
import { OverlayModule } from '@angular/cdk/overlay';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

import { ValidationErrorPipe } from 'src/app/commons/error/validation-error.pipe';

import { StationDTO } from 'src/app/station/station';
import { StationComboboxStore } from 'src/app/station/station-combobox.store';

@Component({
  selector: 'app-station-combobox',
  imports: [
    OverlayModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    TranslatePipe,
    ValidationErrorPipe
  ],
  providers: [StationComboboxStore],
  templateUrl: './station-combobox.html',
  changeDetection: ChangeDetectionStrategy.Eager,
})
export class StationCombobox {

  protected readonly store = inject(StationComboboxStore);

  readonly control = input.required<FormControl<string | null>>();
  readonly label = input.required<string>();
  protected readonly requiredValidator = Validators.required;

  protected readonly isOpen = signal(false);
  protected readonly searchQuery = signal('');
  readonly selectedStation = model<StationDTO | null>(null);

  protected toggleDropdown(): void {

    if (this.isOpen()) {
      this.closeDropdown();
    } else {
      this.openDropdown();
    }

  }

  protected openDropdown(): void {

    if (this.control().disabled || this.isOpen()) {
      return;
    }

    this.searchQuery.set('');
    this.isOpen.set(true);

    this.store.searchStations('');

  }

  protected closeDropdown(): void {

    this.isOpen.set(false);
    this.control().markAsTouched();

  }

  protected onSearchChange(event: Event): void {

    const query = (event.target as HTMLInputElement).value;

    this.searchQuery.set(query);
    this.store.searchStations(query);

  }

  protected selectStation(station: StationDTO): void {

    this.selectedStation.set(station);

    this.control().setValue(station.id);
    this.control().markAsDirty();

    this.closeDropdown();

  }

  protected loadMore(): void {

    this.store.loadMore();

  }

}
