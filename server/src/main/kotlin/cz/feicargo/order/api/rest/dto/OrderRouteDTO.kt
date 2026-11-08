package cz.feicargo.order.api.rest.dto

import cz.feicargo.order.entity.OrderRoute
import kotlinx.serialization.Serializable

@Serializable
data class OrderRouteDTO(
    val id: String,
    val stationFrom: String,
    val stationTo: String,
)

fun OrderRoute.toDTO() = OrderRouteDTO(
    id = this.id.value.toString(),
    stationFrom = this.stationFrom.toString(),
    stationTo = this.stationTo.toString(),
)
