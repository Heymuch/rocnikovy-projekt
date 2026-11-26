package cz.feicargo.order.api.rest.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderCargoUpdateDTO(
    val cargoTypeId: Long,
    val volume: Double,
    val weight: Double,
)

@Serializable
data class OrderRouteUpdateDTO(
    val stationFrom: String,
    val stationTo: String,
)

@Serializable
data class OrderUpdateRequestDTO(
    val name: String,
    val preferredDeparture: String,
    val preferredArrival: String,
    val route: OrderRouteUpdateDTO,
    val cargo: OrderCargoUpdateDTO,
)
