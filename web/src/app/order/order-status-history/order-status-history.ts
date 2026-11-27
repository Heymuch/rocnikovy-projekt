import {
  Component,
  input,
  ChangeDetectionStrategy
} from '@angular/core';

import { NgClass } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

import { OrderStatusHistoryDTO } from 'src/app/order/order';
import { DaytimeDisplayPipe } from 'src/app/commons/daytime-display.pipe';
import { OrderStatusDisplayPipe } from 'src/app/order/order-status/order-status-display.pipe';

@Component({
  selector: 'app-order-status-history',
  standalone: true,
  imports: [
    NgClass,
    MatIconModule,
    TranslatePipe,
    OrderStatusDisplayPipe,
    DaytimeDisplayPipe
  ],
  templateUrl: './order-status-history.html',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class OrderStatusHistory {

  readonly history = input.required<OrderStatusHistoryDTO[]>();

}
