package cz.feicargo.order.usecase

import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.request.OrderCreateRequestDTO
import cz.feicargo.order.api.rest.dto.response.OrderDetailResponseDTO
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.entity.Order
import cz.feicargo.order.entity.OrderCargo
import cz.feicargo.order.entity.OrderRoute
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.wagon.persistence.WagonRepository
import java.math.BigDecimal
import java.time.Instant
import kotlin.math.ceil
import kotlin.uuid.Uuid

class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val cargoTypeRepository: CargoTypeRepository,
    private val wagonRepository: WagonRepository,
    private val transactionManager: TransactionManager,
) : UseCase<CreateOrderUseCase.Context, CreateOrderUseCase.Result> {

    data class Context(
        val userId: Uuid,
        val requestDTO: OrderCreateRequestDTO,
    ) : UseCase.Context

    data class Result(
        val detail: OrderDetailResponseDTO,
    ) : UseCase.Result

    companion object {
        private val CARGO_TO_WAGON = mapOf(
            "WOOD" to "Roos",
            "COAL" to "Eaos",
            "IRON_ORE" to "Falns",
            "BULK_MATERIALS" to "Tadns",
            "LIQUID_CHEMICALS" to "Zacns",
        )
    }

    override suspend fun invoke(context: Context): Result {
        val request = context.requestDTO
        val now = Instant.now()

        require(request.name.isNotBlank()) { "Order name must not be blank." }

        val preferredDeparture = Instant.parse(request.preferredDeparture)
        val preferredArrival = Instant.parse(request.preferredArrival)
        require(preferredDeparture.isAfter(now)) { "Preferred departure must be in the future." }
        require(preferredArrival.isAfter(preferredDeparture)) { "Preferred arrival must be after preferred departure." }

        val stationFrom = Uuid.parse(request.route.stationFrom)
        val stationTo = Uuid.parse(request.route.stationTo)
        require(stationFrom != stationTo) { "Origin and destination stations must be different." }

        require(request.cargo.volume > 0) { "Cargo volume must be greater than zero." }
        require(request.cargo.weight > 0) { "Cargo weight must be greater than zero." }

        val order = transactionManager.newTransaction {
            val cargoType = cargoTypeRepository.findById(request.cargo.cargoTypeId)
                ?: throw IllegalArgumentException("Cargo type with ID ${request.cargo.cargoTypeId} was not found.")
            val wagonName = CARGO_TO_WAGON[cargoType.name.value]
                ?: throw IllegalStateException("No wagon mapping configured for cargo type ${cargoType.name.value}.")
            val wagon = wagonRepository.findByName(wagonName)
                ?: throw IllegalStateException("Wagon $wagonName was not found in database.")

            val wagonsByWeight = ceil(request.cargo.weight / wagon.netCapacityWeight).toInt()
            val wagonsByVolume = ceil(request.cargo.volume / wagon.volumeCapacity).toInt()
            val calculatedWagonCount = maxOf(1, wagonsByWeight, wagonsByVolume)

            val orderId = Uuid.random()

            // TODO: Vypočítat reálnou cenu na základě vzdálenosti trasy, calculatedWagonCount a wagon.rentPrice
            val calculatedPrice = BigDecimal("15000.00")

            val newOrder = Order(
                id = Order.Id(orderId),
                userId = context.userId,
                trainSetId = null,
                name = request.name.trim(),
                status = "IN_QUEUE",
                code = "ORD-${orderId.toString().substring(0, 8).uppercase()}",
                totalPrice = calculatedPrice,
                loadDate = null,
                unloadDate = null,
                preferredDeparture = preferredDeparture,
                preferredArrival = preferredArrival,
                route = OrderRoute(
                    id = OrderRoute.Id(Uuid.random()),
                    orderId = orderId,
                    stationFrom = stationFrom,
                    stationTo = stationTo,
                ),
                cargo = OrderCargo(
                    id = OrderCargo.Id(Uuid.random()),
                    orderId = orderId,
                    cargoTypeId = cargoType.id.value,
                    volume = request.cargo.volume,
                    weight = request.cargo.weight,
                    wagonId = wagon.id.value,
                    wagonCount = calculatedWagonCount,
                ),
            )

            orderRepository.create(newOrder)
        }

        // TODO: Spustit algoritmus pro skládání objednávek do vlaků (nebo jako plánovanou úlohu, která poběží každých X minut/hodin/dní)

        return Result(OrderDetailResponseDTO(order.toDTO(), extend = null))
    }
}
