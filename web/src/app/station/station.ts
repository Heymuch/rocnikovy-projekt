export type StationDTO = {
  id: string
  name: string
}

export type PaginatedStationResponseDTO = {
  items: StationDTO[]
  page: number
  size: number
  totalCount: number
  totalPages: number
}
