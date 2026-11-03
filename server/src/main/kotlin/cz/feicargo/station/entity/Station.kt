package cz.feicargo.station.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class Station(
    val id: Id,
    val name: Name,
) : Entity {
    @JvmInline
    value class Id(
        val value: Uuid,
    )

    @JvmInline
    value class Name(
        val value: String,
    )
}
