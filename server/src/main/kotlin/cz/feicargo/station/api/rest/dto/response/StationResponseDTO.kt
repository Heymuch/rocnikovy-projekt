package cz.feicargo.station.api.rest.dto.response

import cz.feicargo.station.api.rest.dto.StationDTO
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class StationResponseDTO(
    val items: List<StationDTO>,
    val timestamp: Instant = Clock.System.now()
)
