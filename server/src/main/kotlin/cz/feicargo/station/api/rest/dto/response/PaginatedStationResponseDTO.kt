package cz.feicargo.station.api.rest.dto.response

import cz.feicargo.station.api.rest.dto.StationDTO
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedStationResponseDTO(
    val items: List<StationDTO>,
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val totalPages: Int,
)
