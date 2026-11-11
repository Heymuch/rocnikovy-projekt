import {AfterViewInit, Component, inject} from '@angular/core';
import {Location, NgClass} from '@angular/common';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatDivider} from '@angular/material/list';
import {DaytimeDisplayPipe} from 'src/app/commons/daytime-display.pipe';
import {TranslatePipe} from '@ngx-translate/core';
import * as L from 'leaflet';
import {OrderStatusDisplayPipe} from 'src/app/order/order-status-display.pipe';
import {OrderStatus} from 'src/app/order/order-status';
import {Order} from 'src/app/order';

interface TrainMock {
  id: string;
  code: string;
  status: OrderStatus;
  total_price: number;
  total_wagons: number;
  locomotive_type: string;
  wagon_type: string;
  route: {
    station_from_name: string;
    station_to_name: string;
    distance_km: number;
  };
  actual_departure: string;
  actual_arrival: string;
  expiration_date: string;
  assigned_orders: Order[];
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
    OrderStatusDisplayPipe,
    OrderStatusDisplayPipe
  ],
  templateUrl: './train-detail.html',
  styleUrl: './train-detail.scss',
})
export class TrainDetail implements AfterViewInit {
  private location = inject(Location);

  // Mockovaná data pro Vlak
  train: TrainMock = {
    id: 'TS-9988',
    code: 'TS-9988',
    status: OrderStatus.IN_TRANSIT,
    total_price: 135000,
    total_wagons: 25,
    locomotive_type: 'Siemens Vectron MS',
    wagon_type: 'Eas-u / Shimmns',
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

  ngAfterViewInit(): void {
    this.initMap();
  }

  // Inicializace mapy a vrstev pro OpenStreetMap a OpenRailwayMap
  private initMap(): void {
    const map = L.map('map').setView([49.8, 15.5], 7);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(map);

    L.tileLayer('https://{s}.tiles.openrailwaymap.org/standard/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenRailwayMap'
    }).addTo(map);

    setTimeout(() => {
      map.invalidateSize();
    }, 100);
  }

  goBack(): void {
    this.location.back();
  }
}
