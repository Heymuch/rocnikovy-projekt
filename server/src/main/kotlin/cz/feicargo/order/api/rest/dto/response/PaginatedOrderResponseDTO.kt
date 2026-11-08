package cz.feicargo.order.api.rest.dto.response

import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import cz.feicargo.order.api.rest.dto.OrderDTO
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import kotlinx.serialization.Serializable

@Serializable
data class OrderExtendDTO(
    val trainSets: List<TrainSetDTO>? = null,
    val cargoTypes: List<CargoTypeDTO>? = null,
    val stations: List<StationDTO>? = null,
)

@Serializable
data class PaginatedOrderResponseDTO(
    val items: List<OrderDTO>,
    val extend: OrderExtendDTO? = null,
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val totalPages: Int,
)
