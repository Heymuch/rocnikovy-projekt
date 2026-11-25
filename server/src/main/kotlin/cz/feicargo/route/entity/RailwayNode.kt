package cz.feicargo.route.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class RailwayNode(
    val id: Id,
    val latitude: Double,
    val longitude: Double,
) : Entity {
    @JvmInline
    value class Id(val value: Uuid)
}
