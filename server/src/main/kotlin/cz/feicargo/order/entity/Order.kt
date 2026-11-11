package cz.feicargo.order.entity

import cz.feicargo.commons.entity.Entity
import java.math.BigDecimal
import java.time.Instant
import kotlin.uuid.Uuid

data class Order(
    val id: Id,
    val userId: Uuid,
    val trainSetId: Uuid?,
    val name: String,
    val status: String,
    val code: String,
    val totalPrice: BigDecimal,
    val loadDate: Instant,
    val unloadDate: Instant,
    val preferredDeparture: Instant,
    val preferredArrival: Instant,
    val route: OrderRoute?,
    val cargo: OrderCargo?
) : Entity {
    @JvmInline value class Id(val value: Uuid)
}
