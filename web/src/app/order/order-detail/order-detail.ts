import { Component, inject } from '@angular/core';
import {Location, NgClass, NgIf} from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDivider } from '@angular/material/list';
import { OrderStatus } from 'src/app/order/order-status';
import { DaytimeDisplayPipe } from 'src/app/commons/daytime-display.pipe';
import { OrderStatusDisplayPipe } from 'src/app/order/order-status-display.pipe';
import { TranslatePipe } from '@ngx-translate/core';

interface OrderMock {
  id: string;
  code: string;
  status: OrderStatus;
  total_price: number;
  cargo: {
    cargo_type_name: string;
    volume: number;
    weight: number;
    wagon_type_name?: string;
    wagon_count?: number;
  };
  route: {
    station_from_name: string;
    station_to_name: string;
  };
  preferred_departure: string;
  preferred_arrival: string;
  load_date: string;
  unload_date: string;
  actual_departure: string;
  actual_arrival: string;
  // Přidáno pro admina
  user?: {
    name: string;
    email: string;
  };
  train_set_id?: string;
}

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
    NgIf
  ],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.scss',
})
export class OrderDetail {
  private location = inject(Location);

  // Přepínač pro testování admin zobrazení
  isAdmin = true;

  // Pro otestování jiného zobrazení stačí změnit status na 'Potvrzená'
  order: OrderMock = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    code: 'ORD-2026-001',
    status: OrderStatus.COMPLETED, // Zkuste změnit na OrderStatus.IN_QUEUE
    total_price: 45000,
    cargo: {
      cargo_type_name: 'Dřevo',
      volume: 120,
      weight: 80,
      wagon_type_name: 'Eaos (otevřený vůz)', // Doplněno pro pozdější stavy
      wagon_count: 5
    },
    route: {
      station_from_name: 'Praha hl.n.',
      station_to_name: 'Brno hl.n.'
    },
    preferred_departure: '2026-11-01T08:00:00',
    preferred_arrival: '2026-11-02T16:00:00',
    // Doplněno pro zobrazení ostatních stavů
    load_date: '2026-11-01T06:00:00',
    unload_date: '2026-11-02T18:00:00',
    actual_departure: '2026-11-01T09:15:00',
    actual_arrival: '2026-11-02T15:45:00',
    user: {
      name: 'Jan Novák',
      email: 'jan.novak@example.com'
    },
    train_set_id: 'TS-9988'
  };

  goBack(): void {
    this.location.back();
  }

  protected readonly OrderStatus = OrderStatus;
}
