package cz.feicargo.cargotype.persistence.exposed

import cz.feicargo.cargotype.entity.CargoType
import cz.feicargo.cargotype.persistence.CargoTypeRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.r2dbc.select

class CargoTypeExposedRepository : CargoTypeRepository {
    private fun selectQuery() = CargoTypeTable.select(CargoTypeTable.id, CargoTypeTable.name)

    override suspend fun selectAll(): List<CargoType> {
        return selectQuery()
            .map(ResultRow::toCargoType)
            .toList()
    }
}
