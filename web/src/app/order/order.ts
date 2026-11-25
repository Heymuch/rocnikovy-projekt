import { OrderStatus } from 'src/app/order/order-status';
import {
  TrainSetDTO,
  WagonDTO,
  UserDTO
} from 'src/app/train-set/train-set';
import { StationDTO } from 'src/app/station/station';
import { CargoDTO, CargoTypeDTO } from 'src/app/cargo/cargo';

export type RouteDTO = {
  id: string
  stationFrom: string
  stationTo: string
  routePoints?: string[]
}

export type OrderDTO = {
  id: string
  userId: string
  trainSetId?: string | null
  name: string
  status: OrderStatus
  code: string
  totalPrice: number
  loadDate: string
  unloadDate: string
  preferredDeparture: string
  preferredArrival: string
  route: RouteDTO
  cargo: CargoDTO
}

export type OrderExtendDTO = {
  trainSets?: TrainSetDTO[]
  cargoTypes?: CargoTypeDTO[]
  stations?: StationDTO[]
}

export type OrderOverviewResponseDTO = {
  items: OrderDTO[]
  extend: OrderExtendDTO
  page: number
  size: number
  totalCount: number
  totalPages: number
}

// UI modely - hlavní pro Frontend
export type OrderOverview = OrderDTO & {
 stationFrom?: StationDTO | null
 stationTo?: StationDTO | null
 trainSet?: TrainSetDTO | null
 cargoType: CargoTypeDTO | null
}

export type OrderDetailDTO = Omit<
  OrderDTO,
  'loadDate' | 'unloadDate' | 'route' | 'cargo'
> & {
  loadDate: string | null
  unloadDate: string | null
  route: RouteDTO | null
  cargo: CargoDTO | null
}

export type OrderDetailExtendDTO = {
  trainSets?: TrainSetDTO[] | null
  cargoTypes?: CargoTypeDTO[] | null
  stations?: StationDTO[] | null
  wagons?: WagonDTO[] | null
  users?: UserDTO[] | null
}

export type OrderDetailResponseDTO = {
  order: OrderDetailDTO
  extend?: OrderDetailExtendDTO | null
}

// UI model detailu objednávky
export type OrderDetail = OrderDetailDTO & {
  stationFrom: StationDTO | null
  stationTo: StationDTO | null
  trainSet: TrainSetDTO | null
  cargoType: CargoTypeDTO | null
  wagon: WagonDTO | null
  user: UserDTO | null
}
