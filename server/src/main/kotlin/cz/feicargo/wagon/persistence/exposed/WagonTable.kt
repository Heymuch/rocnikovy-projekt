package cz.feicargo.wagon.persistence.exposed

import cz.feicargo.wagon.entity.Wagon
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object WagonTable : LongIdTable("wagons") {
    val name = text("name")
}
fun ResultRow.toWagon() = Wagon(
    id = this[WagonTable.id].value.let(Wagon::Id),
    name = this[WagonTable.name]
)
