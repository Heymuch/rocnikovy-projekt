package cz.feicargo.order.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.request.OrderCreateRequestDTO
import cz.feicargo.order.api.rest.dto.response.OrderDetailResponseDTO
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.entity.Order
import cz.feicargo.order.entity.OrderCargo
import cz.feicargo.order.entity.OrderRoute
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.order.service.OrderCalculationService
import cz.feicargo.order.validation.OrderValidator
import java.time.Instant
import kotlin.uuid.Uuid

class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val orderCalculationService: OrderCalculationService,
    private val transactionManager: TransactionManager,
) : UseCase<CreateOrderUseCase.Context, CreateOrderUseCase.Result> {

    data class Context(
        val userId: Uuid,
        val requestDTO: OrderCreateRequestDTO,
    ) : UseCase.Context

    data class Result(
        val detail: OrderDetailResponseDTO,
    ) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val request = context.requestDTO

        val preferredDeparture = Instant.parse(request.preferredDeparture)
        val preferredArrival = Instant.parse(request.preferredArrival)

        OrderValidator.validateOrderDetails(
            name = request.name,
            preferredDeparture = preferredDeparture,
            preferredArrival = preferredArrival,
        )

        val stationFrom = Uuid.parse(request.route.stationFrom)
        val stationTo = Uuid.parse(request.route.stationTo)

        val calculation = orderCalculationService.calculate(
            stationFrom = stationFrom,
            stationTo = stationTo,
            cargoTypeId = request.cargo.cargoTypeId,
            volume = request.cargo.volume,
            weight = request.cargo.weight,
        )

        val order = transactionManager.newTransaction {
            val orderId = Uuid.random()

            val newOrder = Order(
                id = Order.Id(orderId),
                userId = context.userId,
                trainSetId = null,
                name = request.name.trim(),
                status = "IN_QUEUE",
                code = "ORD-${orderId.toString().substring(0, 8).uppercase()}",
                totalPrice = calculation.totalPrice,
                locomotivePrice = calculation.maxLocomotivePrice,
                loadDate = null,
                unloadDate = null,
                preferredDeparture = preferredDeparture,
                preferredArrival = preferredArrival,
                route = OrderRoute(
                    id = OrderRoute.Id(Uuid.random()),
                    orderId = orderId,
                    stationFrom = stationFrom,
                    stationTo = stationTo,
                    route = calculation.routeNodes,
                ),
                cargo = OrderCargo(
                    id = OrderCargo.Id(Uuid.random()),
                    orderId = orderId,
                    cargoTypeId = calculation.cargoType.id.value,
                    volume = request.cargo.volume,
                    weight = request.cargo.weight,
                    wagonId = calculation.wagon.id.value,
                    wagonCount = calculation.wagonCount,
                ),
            )

            orderRepository.create(newOrder)
        }

        // TODO: Spustit algoritmus pro skládání objednávek do vlaků

        return Result(
            OrderDetailResponseDTO(
                order = order.toDTO(),
                routeLengthKm = calculation.routeLengthKm,
                extend = null,
            ),
        )
    }
}
