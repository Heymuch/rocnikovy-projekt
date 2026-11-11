package cz.feicargo.order.usecase

import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import cz.feicargo.cargotype.api.rest.dto.toDTO
import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.response.OrderExtendDTO
import cz.feicargo.order.entity.Order
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import cz.feicargo.trainset.api.rest.dto.toDTO
import cz.feicargo.trainset.persistence.TrainSetRepository
import kotlin.uuid.Uuid

class SearchOrdersUseCase(
    private val orderRepository: OrderRepository,
    private val trainSetRepository: TrainSetRepository,
    private val cargoTypeRepository: CargoTypeRepository,
    private val stationRepository: StationRepository,
    private val transactionManager: TransactionManager,
) : UseCase<SearchOrdersUseCase.Context, SearchOrdersUseCase.Result> {

    data class Context(val page: Int, val size: Int, val userId: Uuid?, val includes: List<String>) : UseCase.Context
    data class Result(val orders: List<Order>, val totalCount: Long, val extend: OrderExtendDTO?) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val (orders, total) = transactionManager.newTransaction {
            orderRepository.search(context.page, context.size, context.userId)
        }

        var extendDTO: OrderExtendDTO? = null

        if (orders.isNotEmpty() && context.includes.isNotEmpty()) {
            transactionManager.newTransaction {
                var fetchedTrainSets: List<TrainSetDTO>? = null
                var fetchedCargoTypes: List<CargoTypeDTO>? = null
                var fetchedStations: List<StationDTO>? = null

                if (context.includes.contains("trainSet")) {
                    val trainSetIds = orders.mapNotNull { it.trainSetId }.distinct()
                    fetchedTrainSets = trainSetRepository.selectByIds(trainSetIds)
                        .map { it.toDTO() }
                }

                if (context.includes.contains("cargoType")) {
                    val cargoTypeIds = orders.mapNotNull { it.cargo }.map { it.cargoTypeId }.distinct()
                    fetchedCargoTypes = cargoTypeRepository.selectByIds(cargoTypeIds)
                        .map { it.toDTO() }
                }

                if (context.includes.contains("station")) {
                    val stationIdsRaw = orders.mapNotNull { it.route }
                        .flatMap { listOf(it.stationFrom, it.stationTo) }
                        .distinct()
                    val stationIds = stationIdsRaw.map { Station.Id(it) }

                    fetchedStations = stationRepository.selectByIds(stationIds)
                        .map { it.toDTO() }
                }

                extendDTO = OrderExtendDTO(
                    trainSets = fetchedTrainSets,
                    cargoTypes = fetchedCargoTypes,
                    stations = fetchedStations
                )
            }
        }

        return Result(orders, total, extendDTO)
    }
}
