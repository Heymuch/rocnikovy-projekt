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
    val wagonFixedPrice: BigDecimal,       // Fixní pronájem vagónů (wagonCount * fixedRentPrice)
    val wagonDistancePrice: BigDecimal,    // Variabilní pronájem vagónů (km * rentPrice * wagonCount)
    val wagonsTotalPrice: BigDecimal,      // Celkem za vagóny (fixní + variabilní)
    val energyPrice: BigDecimal,           // Energie / trakce podle hmotnosti (km * weight * 0.50)
    val maxLocomotivePrice: BigDecimal,    // Fixní cena nejdražší lokomotivy
    val totalPrice: BigDecimal,            // Celková maximální cena
    val remainingWeightCapacity: Double,   // Zbývající volná nosnost v posledním vagónu (t)
    val remainingVolumeCapacity: Double,   // Zbývající volný objem v posledním vagónu (m3)
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
