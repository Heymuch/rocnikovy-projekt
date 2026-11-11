package cz.feicargo.locomotive.entity

import cz.feicargo.commons.entity.Entity

data class Locomotive(
    val id: Id,
    val name: String
) : Entity {
    @JvmInline value class Id(val value: Long)
}
