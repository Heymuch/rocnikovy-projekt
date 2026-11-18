

import {Component, input} from '@angular/core';
import {NgClass} from '@angular/common';
import {MatTableModule} from '@angular/material/table';
import {TranslatePipe} from '@ngx-translate/core';

import {TrainSetOverview} from 'src/app/train-set/train-set';
import { DaytimeDisplayPipe } from '../../commons/daytime-display.pipe';

@Component({
  selector: 'app-train-table',
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
  readonly trains = input.required<TrainSetOverview[]>();

  readonly displayedColumns = [
    'code',
    'route',
    'departureDate',
    'arrivalDate',
    'expirationDate',
    'status'
  ];

  hasDeparted(train: TrainSetOverview): boolean {
    return new Date(train.departureDate).getTime() <= Date.now();
  }
}




