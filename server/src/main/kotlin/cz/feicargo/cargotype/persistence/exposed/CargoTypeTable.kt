package cz.feicargo.cargotype.persistence.exposed

import cz.feicargo.cargotype.entity.CargoType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.ResultRow

object CargoTypeTable : LongIdTable("cargo_types") {
    val name = varchar("name", 255).uniqueIndex()
}

fun ResultRow.toCargoType() = CargoType(
    id = this[CargoTypeTable.id].value.let(CargoType::Id),
    name = this[CargoTypeTable.name].let(CargoType::Name)
)
