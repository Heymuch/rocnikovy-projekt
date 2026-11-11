package cz.feicargo.trainset.api.rest.dto.response

import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import kotlinx.serialization.Serializable

@Serializable
data class TrainSetExtendDTO(
    val stations: List<StationDTO>? = null
)

@Serializable
data class PaginatedTrainSetResponseDTO(
    val items: List<TrainSetDTO>,
    val extend: TrainSetExtendDTO? = null,
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val totalPages: Int,
)
