package cz.feicargo.order.service.implementation

import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.order.service.OrderCalculationResult
import cz.feicargo.order.service.OrderCalculationService
import cz.feicargo.route.service.RouteService
import cz.feicargo.wagon.persistence.WagonRepository
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.uuid.Uuid

class OrderCalculationServiceImpl(
    private val routeService: RouteService,
    private val cargoTypeRepository: CargoTypeRepository,
    private val wagonRepository: WagonRepository,
    private val transactionManager: TransactionManager,
) : OrderCalculationService {

    companion object {
        // Sazba za hmotnost nákladu pro energie (Kč za 1 tunu na 1 km)
        private const val WEIGHT_RATE_PER_TON_KM = 0.50

        private val CARGO_TO_WAGON = mapOf(
            "WOOD" to "Roos",
            "COAL" to "Eaos",
            "IRON_ORE" to "Falns",
            "BULK_MATERIALS" to "Tadns",
            "LIQUID_CHEMICALS" to "Zacns",
        )
    }

    override suspend fun calculate(
        stationFrom: Uuid,
        stationTo: Uuid,
        cargoTypeId: Long,
        volume: Double,
        weight: Double,
    ): OrderCalculationResult {
        require(stationFrom != stationTo) { "Origin and destination stations must be different." }
        require(volume > 0) { "Cargo volume must be greater than zero." }
        require(weight > 0) { "Cargo weight must be greater than zero." }

        val routeNodes = routeService.findRoute(stationFrom, stationTo)
        require(routeNodes.isNotEmpty()) { "No railway route found between the selected stations." }

        val routeLengthKm = routeService.calculateRouteLengthKm(routeNodes)

        val (cargoType, wagon) = transactionManager.newTransaction {
            val foundCargoType = cargoTypeRepository.findById(cargoTypeId)
                ?: throw IllegalArgumentException("Cargo type with ID $cargoTypeId was not found.")

            val wagonName = CARGO_TO_WAGON[foundCargoType.name.value]
                ?: throw IllegalStateException("No wagon mapping configured for cargo type ${foundCargoType.name.value}.")

            val foundWagon = wagonRepository.findByName(wagonName)
                ?: throw IllegalStateException("Wagon $wagonName was not found in database.")

            foundCargoType to foundWagon
        }

        val wagonsByWeight = ceil(weight / wagon.netCapacityWeight).toInt()
        val wagonsByVolume = ceil(volume / wagon.volumeCapacity).toInt()
        val wagonCount = maxOf(1, wagonsByWeight, wagonsByVolume)

        val wagonCost = routeLengthKm * wagon.rentPrice * wagonCount
        val weightCost = routeLengthKm * weight * WEIGHT_RATE_PER_TON_KM
        val totalPrice = BigDecimal.valueOf(wagonCost + weightCost).setScale(2, RoundingMode.HALF_UP)

        return OrderCalculationResult(
            cargoType = cargoType,
            wagon = wagon,
            wagonCount = wagonCount,
            routeNodes = routeNodes,
            routeLengthKm = routeLengthKm,
            totalPrice = totalPrice,
        )
    }
}
