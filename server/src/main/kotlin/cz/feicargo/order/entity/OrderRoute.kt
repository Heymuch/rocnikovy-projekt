package cz.feicargo.order.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class OrderRoute(
    val id: Id,
    val orderId: Uuid,
    val stationFrom: Uuid,
    val stationTo: Uuid,
) : Entity {
    @JvmInline value class Id(val value: Uuid)
}
