package cz.feicargo.order.service.implementation

import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.locomotive.persistence.LocomotiveRepository
import cz.feicargo.order.service.OrderCalculationResult
import cz.feicargo.order.service.OrderCalculationService
import cz.feicargo.order.validation.OrderValidator
import cz.feicargo.route.service.RouteService
import cz.feicargo.wagon.persistence.WagonRepository
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.round
import kotlin.uuid.Uuid

class OrderCalculationServiceImpl(
    private val routeService: RouteService,
    private val cargoTypeRepository: CargoTypeRepository,
    private val wagonRepository: WagonRepository,
    private val locomotiveRepository: LocomotiveRepository,
    private val transactionManager: TransactionManager,
) : OrderCalculationService {

    companion object {
        // Sazba za hrubou hmotnost soupravy pro energie (Kč za 1 tunu na 1 km)
        private const val WEIGHT_RATE_PER_TON_KM = 0.12

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
        OrderValidator.validateCalculationInput(
            stationFrom = stationFrom,
            stationTo = stationTo,
            volume = volume,
            weight = weight,
        )

        val routeNodes = routeService.findRoute(stationFrom, stationTo)
        require(routeNodes.isNotEmpty()) { "No railway route found between the selected stations." }

        val routeLengthKm = routeService.calculateRouteLengthKm(routeNodes)

        val (cargoType, wagon, maxLocomotiveRent) = transactionManager.newTransaction {
            val foundCargoType = cargoTypeRepository.findById(cargoTypeId)
                ?: throw IllegalArgumentException("Cargo type with ID $cargoTypeId was not found.")

            val wagonName = CARGO_TO_WAGON[foundCargoType.name.value]
                ?: throw IllegalStateException("No wagon mapping configured for cargo type ${foundCargoType.name.value}.")

            val foundWagon = wagonRepository.findByName(wagonName)
                ?: throw IllegalStateException("Wagon $wagonName was not found in database.")

            val maxLocoPrice = locomotiveRepository.selectAll()
                .maxOfOrNull { it.fixedRentPrice }
                ?: throw IllegalStateException("No locomotives found in database.")

            Triple(foundCargoType, foundWagon, maxLocoPrice)
        }

        val wagonsByWeight = ceil(weight / wagon.netCapacityWeight).toInt()
        val wagonsByVolume = ceil(volume / wagon.volumeCapacity).toInt()
        val wagonCount = maxOf(1, wagonsByWeight, wagonsByVolume)

        val remainingWeight = round(((wagonCount * wagon.netCapacityWeight) - weight) * 100.0) / 100.0
        val remainingVolume = round(((wagonCount * wagon.volumeCapacity) - volume) * 100.0) / 100.0

        val totalGrossWeight = weight + (wagonCount * wagon.tareWeight)

        val wagonFixedPrice = toMoney(wagon.fixedRentPrice * wagonCount)
        val wagonDistancePrice = toMoney(routeLengthKm * wagon.rentPrice * wagonCount)
        val wagonsTotalPrice = wagonFixedPrice + wagonDistancePrice
        val energyPrice = toMoney(routeLengthKm * totalGrossWeight * WEIGHT_RATE_PER_TON_KM)
        val maxLocomotivePrice = toMoney(maxLocomotiveRent)
        val totalPrice = wagonsTotalPrice + energyPrice + maxLocomotivePrice

        return OrderCalculationResult(
            cargoType = cargoType,
            wagon = wagon,
            wagonCount = wagonCount,
            routeNodes = routeNodes,
            routeLengthKm = routeLengthKm,
            wagonFixedPrice = wagonFixedPrice,
            wagonDistancePrice = wagonDistancePrice,
            wagonsTotalPrice = wagonsTotalPrice,
            energyPrice = energyPrice,
            maxLocomotivePrice = maxLocomotivePrice,
            totalPrice = totalPrice,
            remainingWeightCapacity = remainingWeight,
            remainingVolumeCapacity = remainingVolume,
        )
    }

    private fun toMoney(value: Double): BigDecimal =
        BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)
}
