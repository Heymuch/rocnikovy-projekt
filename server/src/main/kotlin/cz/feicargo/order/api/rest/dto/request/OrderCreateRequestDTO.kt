package cz.feicargo.order.api.rest.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderCargoCreateDTO(
    val cargoTypeId: Long,
    val volume: Double,
    val weight: Double,
)

@Serializable
data class OrderRouteCreateDTO(
    val stationFrom: String,
    val stationTo: String,
)

@Serializable
data class OrderCreateRequestDTO(
    val name: String,
    val preferredDeparture: String,
    val preferredArrival: String,
    val route: OrderRouteCreateDTO,
    val cargo: OrderCargoCreateDTO,
)
