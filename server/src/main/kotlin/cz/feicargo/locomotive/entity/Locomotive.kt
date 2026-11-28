package cz.feicargo.locomotive.entity

import cz.feicargo.commons.entity.Entity

data class Locomotive(
    val id: Id,
    val name: String,
    val fixedRentPrice: Double,
) : Entity {
    @JvmInline value class Id(
        val value: Long,
    )
}
