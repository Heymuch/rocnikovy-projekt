import { Component, inject } from '@angular/core';
import { Location, NgClass } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDivider } from '@angular/material/list';
import { DaytimeDisplayPipe } from 'src/app/commons/daytime-display.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { OrdersTable } from 'src/app/commons/orders-table/orders-table'; // Uprav cestu dle svého projektu
import { Order } from 'src/app/order'; // Zde importujeme model Order pro tabulku
import { OrderStatus } from 'src/app/commons/order-status';
import {OrderStatusDisplayPipe} from 'src/app/commons/order-status-display.pipe';

interface TrainMock {
  id: string;
  code: string;
  status: string; // Nebo TrainStatus enum, pokud máš vlastní pro vlaky
  total_price: number;
  total_wagons: number;
  route: {
    station_from_name: string;
    station_to_name: string;
    distance_km: number;
  };
  actual_departure: string;
  actual_arrival: string;
  expiration_date: string;
  assigned_orders: Order[]; // Očekává strukturu pro tvou tabulku
}

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
    OrdersTable,
    OrderStatusDisplayPipe,
    // Importujeme tvou existující komponentu!
  ],
  templateUrl: './train-detail.html',
  styleUrl: './train-detail.scss',
})
export class TrainDetail {
  private location = inject(Location);

  // Mockovaná data pro Vlak
  train: TrainMock = {
    id: 'TS-9988',
    code: 'TS-9988',
    status: 'IN_TRANSIT',
    total_price: 135000,
    total_wagons: 25,
    route: {
      station_from_name: 'Praha hl.n.',
      station_to_name: 'Brno hl.n.',
      distance_km: 255
    },
    actual_departure: '2026-11-01T09:15:00',
    actual_arrival: '2026-11-02T15:45:00',
    expiration_date: '2026-10-31T20:00:00',
    assigned_orders: [
      {
        id: 'ORD-2026-001',
        code: 'Dřevo - Praha/Brno',
        status: OrderStatus.IN_TRANSIT,
        cargoType: 'Dřevo',
        stationFrom: 'Praha hl.n.',
        stationTo: 'Brno hl.n.',
        preferredDeparture: new Date('2026-10-20T06:00:00'),
        preferredArrival: new Date('2026-10-20T06:00:00')
      },
      {
        id: 'ORD-2026-002',
        code: 'Stavební materiál',
        status: OrderStatus.IN_TRANSIT,
        cargoType: 'Stavebniny',
        stationFrom: 'Praha hl.n.',
        stationTo: 'Brno hl.n.',
        preferredDeparture: new Date('2026-10-20T06:00:00'),
        preferredArrival: new Date('2026-10-20T06:00:00')
      }
    ]
  };

  goBack(): void {
    this.location.back();
  }
}
