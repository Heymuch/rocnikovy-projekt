import { StationDTO } from 'src/app/station/station';
import {OrderDTO, OrderOverview} from 'src/app/order/order';
import {
  RailwayNodeDTO,
  RoutePoint
} from 'src/app/commons/route-map/route-map.model';
import {CargoTypeDTO} from 'src/app/cargo/cargo';

export type TrainSetDTO = {
  id: string
  code: string
  locomotiveId: number
  departureDate: string
  arrivalDate: string
  expirationDate: string
}

// Data vlakové soupravy z endpointu /train-sets/search
export type TrainSetOverviewDTO = TrainSetDTO & {
  stationFromId: string
  stationToId: string
}

export type TrainSetOverviewResponseDTO = {
  items: TrainSetOverviewDTO[]
  extend: {
    stations?: StationDTO[]
  }
  page: number
  size: number
  totalCount: number
  totalPages: number
}

// UI model - hlavní pro frontend
export type TrainSetOverview = TrainSetOverviewDTO & {
  stationFrom?: StationDTO | null
  stationTo?: StationDTO | null
}

export type LocomotiveDTO = {
  id: number
  name: string
}

export type WagonDTO = {
  id: number
  name: string
}

export type UserDTO = {
  id: string
  name: string
  email: string
}

export type TrainSetOrderDetailDTO = {
  order: OrderDTO
  user: UserDTO
}

export type TrainSetDetailExtendDTO = {
  stations?: StationDTO[] | null
  locomotives?: LocomotiveDTO[] | null
  wagons?: WagonDTO[] | null
  railwayNodes?: RailwayNodeDTO[] | null
  cargoTypes?: CargoTypeDTO[] | null
}

export type TrainSetDetailResponseDTO = {
  id: string
  code: string
  stationFromId: string | null
  stationToId: string | null
  departureDate: string
  arrivalDate: string
  expirationDate: string
  totalWagons: number
  totalPrice: number
  routeLengthKm: number
  route: string[]
  orders: TrainSetOrderDetailDTO[]
  extend?: TrainSetDetailExtendDTO | null
}

// UI model objednávky přiřazené k vlaku
export type TrainSetOrderDetail = OrderOverview & {
  user: UserDTO
}

// UI model detailu vlaku
export type TrainSetDetail =
  Omit<TrainSetDetailResponseDTO, 'orders' | 'extend'> & {
  stationFrom: StationDTO | null
  stationTo: StationDTO | null
  locomotives: LocomotiveDTO[]
  wagons: WagonDTO[]
  orders: TrainSetOrderDetail[]
  routePoints: RoutePoint[]
}
