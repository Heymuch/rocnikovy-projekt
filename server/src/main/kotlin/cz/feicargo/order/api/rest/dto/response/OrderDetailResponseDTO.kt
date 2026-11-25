package cz.feicargo.order.api.rest.dto.response

import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import cz.feicargo.order.api.rest.dto.OrderDTO
import cz.feicargo.route.api.rest.dto.RailwayNodeDTO
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import cz.feicargo.user.api.rest.dto.UserDTO
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailExtendDTO(
    val trainSets: List<TrainSetDTO>? = null,
    val cargoTypes: List<CargoTypeDTO>? = null,
    val stations: List<StationDTO>? = null,
    val wagons: List<WagonDTO>? = null,
    val users: List<UserDTO>? = null,
    val railwayNodes: List<RailwayNodeDTO>? = null,
)

@Serializable
data class OrderDetailResponseDTO(
    val order: OrderDTO,
    val routeLengthKm: Double? = null,
    val extend: OrderDetailExtendDTO? = null
)
