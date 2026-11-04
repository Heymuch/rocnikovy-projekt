package cz.feicargo.station.api.rest.dto

import cz.feicargo.station.entity.Station
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class StationDTO(
    val id: Uuid,
    val name: String,
)

fun Station.toDTO() = StationDTO(
    id = this.id.value,
    name = this.name.value,
)
