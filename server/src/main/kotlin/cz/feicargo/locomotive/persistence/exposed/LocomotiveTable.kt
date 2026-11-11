package cz.feicargo.locomotive.persistence.exposed

import cz.feicargo.locomotive.entity.Locomotive
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object LocomotiveTable : LongIdTable("locomotives") {
    val name = text("name")
}

fun ResultRow.toLocomotive() = Locomotive(
    id = this[LocomotiveTable.id].value.let(Locomotive::Id),
    name = this[LocomotiveTable.name]
)
