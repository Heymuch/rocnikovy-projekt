import {OrderStatus} from 'src/app/order/order-status';
import {CargoTypeDTO, TrainSetDTO} from 'src/app/train-set/train-set';
import {StationDTO} from 'src/app/station/station';

export type RouteDTO = {
  id: string
  stationFrom: string
  stationTo: string
  routePoints?: string[]
}

export type CargoDTO = {
  id: string
  cargoTypeId: number
  volume: number
  weight: number
  wagonId: number
  wagonCount: number
}

export type OrderDTO = {
  id: string
  userId: string
  trainSetId?: string | null
  status: OrderStatus
  code: string
  totalPrice: number
  loadDate: string
  unloadDate: string
  preferredDeparture: string
  preferredArrival: string
  route: RouteDTO
  cargoes: CargoDTO[]
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
 cargoTypes?: CargoTypeDTO[] | null
}
