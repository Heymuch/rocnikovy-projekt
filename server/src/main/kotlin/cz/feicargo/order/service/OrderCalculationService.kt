package cz.feicargo.order.service

import cz.feicargo.cargotype.entity.CargoType
import cz.feicargo.wagon.entity.Wagon
import java.math.BigDecimal
import kotlin.uuid.Uuid

data class OrderCalculationResult(
    val cargoType: CargoType,
    val wagon: Wagon,
    val wagonCount: Int,
    val routeNodes: List<Uuid>,
    val routeLengthKm: Double,
    val totalPrice: BigDecimal,
)

interface OrderCalculationService {
    suspend fun calculate(
        stationFrom: Uuid,
        stationTo: Uuid,
        cargoTypeId: Long,
        volume: Double,
        weight: Double,
    ): OrderCalculationResult
}
