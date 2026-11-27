import { Component, inject, OnInit, ChangeDetectionStrategy, computed } from '@angular/core';
import { Location, NgClass } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDivider } from '@angular/material/list';

import { OrderStatus } from 'src/app/order/order-status/order-status';
import { DaytimeDisplayPipe } from 'src/app/commons/daytime-display.pipe';
import { OrderStatusDisplayPipe } from 'src/app/order/order-status/order-status-display.pipe';
import { TranslatePipe } from '@ngx-translate/core';

import { OrderDetailStore } from 'src/app/order/order-detail/order-detail.store';
import {RouteMap} from 'src/app/commons/route-map/route-map';
import { OrderStatusHistory } from 'src/app/order/order-status-history/order-status-history';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDivider,
    DaytimeDisplayPipe,
    OrderStatusDisplayPipe,
    TranslatePipe,
    NgClass,
    RouterLink,
    RouteMap,
    OrderStatusHistory
  ],
  providers: [OrderDetailStore],
  templateUrl: './order-detail.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './order-detail.scss',
})
export class OrderDetail implements OnInit {
  private readonly location = inject(Location);
  private readonly route = inject(ActivatedRoute);

  readonly store = inject(OrderDetailStore);
  readonly order = this.store.order;

  // TODO: Napojit na skutečnou roli uživatele z Keycloaku.
  isAdmin = true;

  protected readonly OrderStatus = OrderStatus;

  readonly showTransportDetails = computed(() => {
    const status = this.order()?.status;

    return status === OrderStatus.CONFIRMED ||
      status === OrderStatus.IN_TRANSIT ||
      status === OrderStatus.COMPLETED;
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.store.loadOrder({
        id,
        isAdmin: this.isAdmin,
      });
    }
  }

  goBack(): void {
    this.location.back();
  }
}
