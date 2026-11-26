import {
  Component,
  inject,
  OnInit,
  ChangeDetectionStrategy
} from '@angular/core';

import { OrdersTable } from 'src/app/order/orders-table/orders-table';
import { TranslatePipe } from '@ngx-translate/core';
import { OrdersOverviewStore } from 'src/app/order/orders-overview.store';
import {
  MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';
import { PaginatedTableDirective } from 'src/app/commons/directives/paginated-table.directive';

@Component({
  selector: 'app-my-orders',
  imports: [
    OrdersTable,
    TranslatePipe,
    MatPaginatorModule,
    PaginatedTableDirective
  ],
  templateUrl: './my-orders.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './my-orders.scss',
})
export class MyOrders implements OnInit {

  readonly store = inject(OrdersOverviewStore);
  readonly orders = this.store.orders;

  ngOnInit(): void {
    this.store.loadOrders({
      pageNumber: this.store.page(),
      pageSize: this.store.size()
    });
  }

  onPageChange(event: PageEvent): void {
    this.store.changePage(
      event.pageIndex,
      event.pageSize
    );
  }
}
