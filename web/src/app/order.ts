// todo: smazat až budou
import {OrderStatus} from 'src/app/order/order-status';
import {RouteDTO} from 'src/app/order/order';

export type Order = {
  id: string;
  code: string;
  status: OrderStatus;

  cargoType: string;

  stationFrom: string;
  stationTo: string;

  preferredDeparture: Date;
  preferredArrival: Date;

  userId?: string;
  totalPrice?: number;
  loadDate?: Date;
  unloadDate?: Date;
  trainSetId?: string;
  routeId?: string;
  cargoes?: any[];
  route?: RouteDTO

}
