package cz.feicargo.route.api.rest.dto

import cz.feicargo.route.entity.RailwayNode
import kotlinx.serialization.Serializable

@Serializable
data class RailwayNodeDTO(
    val id: String,
    val latitude: Double,
    val longitude: Double,
)

fun RailwayNode.toDTO() = RailwayNodeDTO(
    id = this.id.value.toString(),
    latitude = this.latitude,
    longitude = this.longitude,
)
