package cz.feicargo.order.entity

import cz.feicargo.commons.entity.Entity
import java.time.Instant
import kotlin.uuid.Uuid

data class OrderStatusHistory(
    val id: Id,
    val orderId: Uuid,
    val status: String,
    val changedAt: Instant,
) : Entity {
    @JvmInline
    value class Id(val value: Uuid)
}
