
import { Component, computed, input } from '@angular/core';
import { NgClass } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';

import { TrainSet, Station } from '../train-overview/train-overview';
import { DaytimeDisplayPipe } from '../../commons/daytime-display.pipe';

@Component({
  selector: 'app-train-table',
  standalone: true,
  imports: [
    MatTableModule,
    NgClass,
    TranslatePipe,
    DaytimeDisplayPipe
  ],
  templateUrl: './train-table.html',
  styleUrl: './train-table.scss'
})
export class TrainTable {
  readonly trains = input.required<TrainSet[]>();
  readonly stations = input.required<Station[]>();

  readonly displayedColumns = [
    'code',
    'route',
    'departureDate',
    'arrivalDate',
    'expirationDate',
    'status'
  ];

  private readonly stationMap = computed(() =>
    new Map(
      this.stations().map(station => [station.id, station.name])
    )
  );

  stationName(stationId: string): string {
    return this.stationMap().get(stationId) ?? stationId;
  }

  hasDeparted(train: TrainSet): boolean {
    return new Date(train.departureDate).getTime() <= Date.now();
  }
}
