package cz.feicargo.order.api.rest.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderCalculateRequestDTO(
    val stationFrom: String,
    val stationTo: String,
    val cargoTypeId: Long,
    val volume: Double,
    val weight: Double,
)
