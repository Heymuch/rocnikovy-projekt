import {Component, input} from '@angular/core';
import {Order} from '../../order';
import {DatePipe, NgClass} from '@angular/common';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatNoDataRow,
  MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';

@Component({
  selector: 'app-orders-table',
  imports: [
    DatePipe,
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
    MatNoDataRow
  ],
  templateUrl: './orders-table.html',
  styleUrl: './orders-table.scss',
})
export class OrdersTable {
  readonly orders = input.required<Order[]>();

  displayedColumns: string[] = ['code', 'status', 'cargoType', 'route', 'dates'];

  getStatusLabel(status: string): string {
    const statusMap: Record<string, string> = {
      'QUEUED': 'Čeká ve frontě',
      'CONFIRMED': 'Potvrzena',
      'IN_TRANSIT': 'V přepravě',
      'COMPLETED': 'Dokončena'
    };
    return statusMap[status] || status;
  }
}
