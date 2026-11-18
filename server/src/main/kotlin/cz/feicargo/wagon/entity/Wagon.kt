package cz.feicargo.wagon.entity

import cz.feicargo.commons.entity.Entity

data class Wagon(
    val id: Id,
    val name: String,
    val tareWeight: Double,
    val netCapacityWeight: Double,
    val grossWeightLimit: Double,
    val volumeCapacity: Double,
    val rentPrice: Double,
) : Entity {
    @JvmInline
    value class Id(val value: Long)
}
