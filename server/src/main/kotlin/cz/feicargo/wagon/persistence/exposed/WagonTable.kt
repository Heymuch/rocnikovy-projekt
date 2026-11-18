package cz.feicargo.wagon.persistence.exposed

import cz.feicargo.wagon.entity.Wagon
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object WagonTable : LongIdTable("wagons") {
    val name = text("name")
    val tareWeight = double("tare_weight")
    val netCapacityWeight = double("net_capacity_weight")
    val grossWeightLimit = double("gross_weight_limit")
    val volumeCapacity = double("volume_capacity")
    val rentPrice = double("rent_price")
}

fun ResultRow.toWagon() = Wagon(
    id = this[WagonTable.id].value.let(Wagon::Id),
    name = this[WagonTable.name],
    tareWeight = this[WagonTable.tareWeight],
    netCapacityWeight = this[WagonTable.netCapacityWeight],
    grossWeightLimit = this[WagonTable.grossWeightLimit],
    volumeCapacity = this[WagonTable.volumeCapacity],
    rentPrice = this[WagonTable.rentPrice],
)
