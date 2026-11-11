package cz.feicargo.wagon.entity

import cz.feicargo.commons.entity.Entity

data class Wagon(
    val id: Id,
    val name: String
) : Entity {
    @JvmInline value class Id(val value: Long)
}
