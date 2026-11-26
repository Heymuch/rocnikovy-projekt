package cz.feicargo.order.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.request.OrderUpdateRequestDTO
import cz.feicargo.order.api.rest.dto.response.OrderDetailResponseDTO
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.entity.OrderCargo
import cz.feicargo.order.entity.OrderRoute
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.order.service.OrderCalculationService
import java.time.Instant
import kotlin.uuid.Uuid

class UpdateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val orderCalculationService: OrderCalculationService,
    private val transactionManager: TransactionManager,
) : UseCase<UpdateOrderUseCase.Context, UpdateOrderUseCase.Result> {

    data class Context(
        val orderId: Uuid,
        val userId: Uuid,
        val requestDTO: OrderUpdateRequestDTO,
    ) : UseCase.Context

    sealed interface Result : UseCase.Result {
        data class Success(val detail: OrderDetailResponseDTO) : Result
        data object NotFound : Result
        data object Forbidden : Result
        data class InvalidStatus(val currentStatus: String) : Result
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

        val existingOrder = transactionManager.newTransaction {
            orderRepository.findById(context.orderId)
        } ?: return Result.NotFound

        // TODO: Po napojení Keycloaku povolit úpravu i pro uživatele s rolí ADMIN
        if (existingOrder.userId != context.userId) {
            return Result.Forbidden
        }

        if (existingOrder.status != "IN_QUEUE") {
            return Result.InvalidStatus(existingOrder.status)
        }

        val calculation = orderCalculationService.calculate(
            stationFrom = stationFrom,
            stationTo = stationTo,
            cargoTypeId = request.cargo.cargoTypeId,
            volume = request.cargo.volume,
            weight = request.cargo.weight,
        )

        val updatedOrder = existingOrder.copy(
            name = request.name.trim(),
            totalPrice = calculation.totalPrice,
            preferredDeparture = preferredDeparture,
            preferredArrival = preferredArrival,
            route = OrderRoute(
                id = existingOrder.route?.id ?: OrderRoute.Id(Uuid.random()),
                orderId = existingOrder.id.value,
                stationFrom = stationFrom,
                stationTo = stationTo,
                route = calculation.routeNodes,
            ),
            cargo = OrderCargo(
                id = existingOrder.cargo?.id ?: OrderCargo.Id(Uuid.random()),
                orderId = existingOrder.id.value,
                cargoTypeId = calculation.cargoType.id.value,
                volume = request.cargo.volume,
                weight = request.cargo.weight,
                wagonId = calculation.wagon.id.value,
                wagonCount = calculation.wagonCount,
            ),
        )

        val savedOrder = transactionManager.newTransaction {
            orderRepository.update(updatedOrder)
        }

        return Result.Success(
            OrderDetailResponseDTO(
                order = savedOrder.toDTO(),
                routeLengthKm = calculation.routeLengthKm,
                extend = null,
            ),
        )
    }
}
