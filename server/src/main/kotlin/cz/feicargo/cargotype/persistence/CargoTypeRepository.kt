package cz.feicargo.cargotype.persistence

import cz.feicargo.cargotype.entity.CargoType

interface CargoTypeRepository {
    suspend fun selectAll(): List<CargoType>
}
