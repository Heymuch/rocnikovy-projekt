package cz.feicargo.trainset.api.rest.dto.response

import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class PaginatedTrainSetResponseDTO(
    val items: List<TrainSetDTO>,
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val totalPages: Int,
    val timestamp: Instant = Clock.System.now()
)
