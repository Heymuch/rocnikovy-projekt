package cz.feicargo.order.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class OrderCargo(
    val id: Id,
    val orderId: Uuid,
    val cargoTypeId: Long,
    val volume: Double,
    val weight: Double,
    val wagonId: Long,
    val wagonCount: Int,
) : Entity {
    @JvmInline value class Id(val value: Uuid)
}
