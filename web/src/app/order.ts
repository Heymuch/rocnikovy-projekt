import {OrderStatus} from 'src/app/commons/order-status';

export type Order = {
  id: string;
  code: string;
  status: OrderStatus;

  cargoType: string;

  stationFrom: string;
  stationTo: string;

  preferredDeparture: Date;
  preferredArrival: Date;
}
