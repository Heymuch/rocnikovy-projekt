export type OrderStatus = 'QUEUED' | 'CONFIRMED' | 'IN_TRANSIT' | 'COMPLETED';

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
