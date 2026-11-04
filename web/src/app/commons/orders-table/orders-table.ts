import {Component, input} from '@angular/core';
import {Order} from '../../order';
import {NgClass} from '@angular/common';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatNoDataRow,
  MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {TranslatePipe} from '@ngx-translate/core';
import {DaytimeDisplayPipe} from 'src/app/commons/daytime-display.pipe';
import {OrderStatusDisplayPipe} from '../order-status-display.pipe';

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
    OrderStatusDisplayPipe
  ],
  templateUrl: './orders-table.html',
  styleUrl: './orders-table.scss',
})
export class OrdersTable {
  readonly orders = input.required<Order[]>();
  displayedColumns: string[] = ['code', 'status', 'cargoType', 'route', 'dates'];
}
