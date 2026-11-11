package cz.feicargo.trainset.api.rest.dto.response

import cz.feicargo.order.api.rest.dto.OrderDTO
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.user.api.rest.dto.UserDTO
import cz.feicargo.locomotive.api.rest.dto.LocomotiveDTO
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import kotlinx.serialization.Serializable

@Serializable
data class TrainSetOrderDetailDTO(
    val order: OrderDTO,
    val user: UserDTO,
)

@Serializable
data class TrainSetDetailExtendDTO(
    val stations: List<StationDTO>? = null,
    val locomotives: List<LocomotiveDTO>? = null,
    val wagons: List<WagonDTO>? = null
)

@Serializable
data class TrainSetDetailResponseDTO(
    val id: String,
    val code: String,
    val stationFromId: String?,
    val stationToId: String?,
    val departureDate: String,
    val arrivalDate: String,
    val expirationDate: String,
    val totalWagons: Int,
    val totalPrice: Double,
    val routeLengthKm: Double,
    val route: List<String>,
    val orders: List<TrainSetOrderDetailDTO>,
    val extend: TrainSetDetailExtendDTO? = null
)
