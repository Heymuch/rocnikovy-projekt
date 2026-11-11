import {Component, inject, OnInit} from '@angular/core';
import {OrdersTable} from 'src/app/order/orders-table/orders-table';
import {TranslatePipe} from '@ngx-translate/core';
import {OrdersOverviewStore} from 'src/app/order/orders-overview.store';

@Component({
  selector: 'app-my-orders',
  imports: [
    OrdersTable,
    TranslatePipe
  ],
  templateUrl: './my-orders.html',
  styleUrl: './my-orders.scss',
})
export class MyOrders implements OnInit {
  readonly store = inject(OrdersOverviewStore)
  readonly orders = this.store.orders

  ngOnInit(): void {
    // todo: pages i includes přesunout do service, nedávat napevno
    this.store.loadOrders({
      pageNumber: 0,
      pageSize: 25,
      includes: ['trainSet', 'cargoType', 'station']
    });
  }
}
