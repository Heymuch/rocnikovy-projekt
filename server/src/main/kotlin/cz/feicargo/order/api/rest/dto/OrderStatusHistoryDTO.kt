package cz.feicargo.order.api.rest.dto

import cz.feicargo.order.entity.OrderStatusHistory
import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusHistoryDTO(
    val id: String,
    val status: String,
    val changedAt: String,
)

fun OrderStatusHistory.toDTO() = OrderStatusHistoryDTO(
    id = this.id.value.toString(),
    status = this.status,
    changedAt = this.changedAt.toString(),
)
