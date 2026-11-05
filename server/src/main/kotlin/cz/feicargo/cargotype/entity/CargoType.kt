package cz.feicargo.cargotype.entity

import cz.feicargo.commons.entity.Entity

data class CargoType(
    val id: Id,
    val name: Name,
) : Entity {
    @JvmInline value class Id(val value: Long)
    @JvmInline value class Name(val value: String)
}
