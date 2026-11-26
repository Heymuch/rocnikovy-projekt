package cz.feicargo.order.api.rest.dto.response

import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import cz.feicargo.route.api.rest.dto.RailwayNodeDTO
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import kotlinx.serialization.Serializable

@Serializable
data class OrderCalculateExtendDTO(
    val railwayNodes: List<RailwayNodeDTO>? = null,
)

@Serializable
data class OrderCalculateResponseDTO(
    val totalPrice: Double,
    val routeLengthKm: Double,
    val wagonCount: Int,
    val wagon: WagonDTO,
    val cargoType: CargoTypeDTO,
    val route: List<String>,
    val extend: OrderCalculateExtendDTO? = null,
)
