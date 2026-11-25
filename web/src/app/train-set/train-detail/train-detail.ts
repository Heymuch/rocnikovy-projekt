import {Component, inject, OnInit} from '@angular/core';
import {Location, NgClass} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatDivider} from '@angular/material/list';
import {DaytimeDisplayPipe} from 'src/app/commons/daytime-display.pipe';
import {TranslatePipe} from '@ngx-translate/core';

import {OrdersTable} from 'src/app/order/orders-table/orders-table';
import {TrainDetailStore} from 'src/app/train-set/train-detail.store';
import {TrainSetDetail} from 'src/app/train-set/train-set';

@Component({
  selector: 'app-train-detail',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDivider,
    DaytimeDisplayPipe,
    TranslatePipe,
    NgClass,
    OrdersTable
  ],
  providers: [TrainDetailStore],
  templateUrl: './train-detail.html',
  styleUrl: './train-detail.scss',
})
export class TrainDetail implements OnInit {

  private readonly location = inject(Location);
  private readonly route = inject(ActivatedRoute);

  readonly store = inject(TrainDetailStore);
  readonly train = this.store.train;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.store.loadTrain(id);
    }
  }

  hasDeparted(train: TrainSetDetail): boolean {
    return new Date(train.departureDate).getTime() <= Date.now();
  }

  getLocomotiveNames(train: TrainSetDetail): string {
    return train.locomotives.map(locomotive => locomotive.name)
      .join(', ') || '—';
  }

  getWagonNames(train: TrainSetDetail): string {
    return train.wagons.map(wagon => wagon.name)
      .join(', ') || '—';
  }

  goBack(): void {
    this.location.back();
  }
}
