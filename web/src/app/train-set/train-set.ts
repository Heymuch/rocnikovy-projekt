
import {StationDTO} from 'src/app/station/station';

export type TrainSetDTO = {
  id: string
  code: string
  locomotiveId: number
  departureDate: string
  arrivalDate: string
  expirationDate: string
}

export type CargoTypeDTO = {
  id: number
  name: string
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
