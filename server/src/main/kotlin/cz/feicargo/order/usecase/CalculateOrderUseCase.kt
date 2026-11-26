package cz.feicargo.order.usecase

import cz.feicargo.cargotype.api.rest.dto.toDTO
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.request.OrderCalculateRequestDTO
import cz.feicargo.order.api.rest.dto.response.OrderCalculateExtendDTO
import cz.feicargo.order.api.rest.dto.response.OrderCalculateResponseDTO
import cz.feicargo.order.service.OrderCalculationService
import cz.feicargo.route.api.rest.dto.toDTO
import cz.feicargo.route.persistence.RailwayNodeRepository
import cz.feicargo.wagon.api.rest.dto.toDTO
import kotlin.uuid.Uuid

class CalculateOrderUseCase(
    private val orderCalculationService: OrderCalculationService,
    private val railwayNodeRepository: RailwayNodeRepository,
    private val transactionManager: TransactionManager,
) : UseCase<CalculateOrderUseCase.Context, CalculateOrderUseCase.Result> {

    data class Context(
        val requestDTO: OrderCalculateRequestDTO,
        val includes: List<String>,
    ) : UseCase.Context

    data class Result(
        val calculation: OrderCalculateResponseDTO,
    ) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val request = context.requestDTO

        val stationFrom = Uuid.parse(request.stationFrom)
        val stationTo = Uuid.parse(request.stationTo)

        val calculation = orderCalculationService.calculate(
            stationFrom = stationFrom,
            stationTo = stationTo,
            cargoTypeId = request.cargoTypeId,
            volume = request.volume,
            weight = request.weight,
        )

        val extendDTO = if (context.includes.contains("railwayNode")) {
            val railwayNodes = transactionManager.newTransaction {
                railwayNodeRepository.selectByIds(calculation.routeNodes.distinct()).map { it.toDTO() }
            }
            OrderCalculateExtendDTO(railwayNodes = railwayNodes)
        } else {
            null
        }

        val responseDTO = OrderCalculateResponseDTO(
            totalPrice = calculation.totalPrice.toDouble(),
            routeLengthKm = calculation.routeLengthKm,
            wagonCount = calculation.wagonCount,
            wagon = calculation.wagon.toDTO(),
            cargoType = calculation.cargoType.toDTO(),
            route = calculation.routeNodes.map { it.toString() },
            extend = extendDTO,
        )

        return Result(responseDTO)
    }
}
