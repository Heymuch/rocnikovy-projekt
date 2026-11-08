package cz.feicargo.order.api.rest.dto

import cz.feicargo.order.entity.OrderCargo
import kotlinx.serialization.Serializable

@Serializable
data class OrderCargoDTO(
    val id: String,
    val cargoTypeId: Long,
    val volume: Double,
    val weight: Double,
    val wagonId: Long,
    val wagonCount: Int,
)

fun OrderCargo.toDTO() = OrderCargoDTO(
    id = this.id.value.toString(),
    cargoTypeId = this.cargoTypeId,
    volume = this.volume,
    weight = this.weight,
    wagonId = this.wagonId,
    wagonCount = this.wagonCount,
)
