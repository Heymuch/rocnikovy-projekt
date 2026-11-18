export type CargoDTO = {
  id: string
  cargoTypeId: number
  volume: number
  weight: number
  wagonId: number
  wagonCount: number
}
export type CargoTypeDTO = {
  id: number
  name: string
}

export type CargoTypesResponseDTO = {
  items: CargoTypeDTO[]
}
