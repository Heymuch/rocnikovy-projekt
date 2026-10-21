import { Component } from '@angular/core';
import {Order} from '../order';
import {OrdersTable} from '../commons/orders-table/orders-table';

@Component({
  selector: 'app-my-orders',
  imports: [
    OrdersTable
  ],
  templateUrl: './my-orders.html',
  styleUrl: './my-orders.scss',
})
export class MyOrders {
  mockOrders: Order[] = [
    {
      id: 'ORD-001',
      code: 'Přeprava uhlí Ostrava',
      status: 'QUEUED',
      cargoType: 'Sypký materiál',
      stationFrom: 'Ostrava',
      stationTo: 'Kladno',
      preferredDeparture: new Date('2026-10-25T08:00:00'),
      preferredArrival: new Date('2026-10-26T18:00:00')
    },
    {
      id: 'ORD-002',
      code: 'Dřevo na pilu',
      status: 'IN_TRANSIT',
      cargoType: 'Dřevo',
      stationFrom: 'Liberec',
      stationTo: 'Brno',
      preferredDeparture: new Date('2026-10-20T06:00:00'),
      preferredArrival: new Date('2026-10-21T12:00:00')
    },
    {
      id: 'ORD-003',
      code: 'Stavební ocel',
      status: 'COMPLETED',
      cargoType: 'Kovovýroba',
      stationFrom: 'Plzeň',
      stationTo: 'Praha',
      preferredDeparture: new Date('2026-10-15T09:00:00'),
      preferredArrival: new Date('2026-10-15T15:00:00')
    }
  ];
}
