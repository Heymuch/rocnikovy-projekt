import { Component, input, ChangeDetectionStrategy } from '@angular/core';

import { NgClass } from '@angular/common';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatNoDataRow,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { DaytimeDisplayPipe } from 'src/app/commons/daytime-display.pipe';
import { OrderStatusDisplayPipe } from 'src/app/order/order-status/order-status-display.pipe';
import { OrderOverview } from 'src/app/order/order';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-orders-table',
  imports: [
    NgClass,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatNoDataRow,
    TranslatePipe,
    DaytimeDisplayPipe,
    OrderStatusDisplayPipe,
    RouterLink,
  ],
  templateUrl: './orders-table.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './orders-table.scss',
})
export class OrdersTable {
  readonly orders = input.required<OrderOverview[]>();
  displayedColumns: string[] = [
    'code',
    'name',
    'status',
    'cargoType',
    'route',
    'departure',
    'arrival',
  ];
}
