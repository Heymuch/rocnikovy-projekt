package cz.feicargo.order.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.api.rest.dto.response.OrderDetailExtendDTO
import cz.feicargo.order.api.rest.dto.response.OrderDetailResponseDTO
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.trainset.api.rest.dto.TrainSetDTO
import cz.feicargo.trainset.api.rest.dto.toDTO
import cz.feicargo.trainset.persistence.TrainSetRepository
import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import cz.feicargo.cargotype.api.rest.dto.toDTO
import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.route.api.rest.dto.RailwayNodeDTO
import cz.feicargo.route.api.rest.dto.toDTO
import cz.feicargo.route.persistence.RailwayNodeRepository
import cz.feicargo.route.service.RouteService
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import cz.feicargo.wagon.api.rest.dto.toDTO
import cz.feicargo.wagon.persistence.WagonRepository
import cz.feicargo.user.api.rest.dto.UserDTO
import cz.feicargo.user.api.rest.dto.toDTO
import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import kotlin.uuid.Uuid

class GetOrderDetailUseCase(
    private val orderRepository: OrderRepository,
    private val trainSetRepository: TrainSetRepository,
    private val cargoTypeRepository: CargoTypeRepository,
    private val stationRepository: StationRepository,
    private val wagonRepository: WagonRepository,
    private val userRepository: UserRepository,
    private val railwayNodeRepository: RailwayNodeRepository,
    private val routeService: RouteService,
    private val transactionManager: TransactionManager,
) : UseCase<GetOrderDetailUseCase.Context, GetOrderDetailUseCase.Result> {

    data class Context(val orderId: Uuid, val includes: List<String>) : UseCase.Context
    data class Result(val detail: OrderDetailResponseDTO?) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val response = transactionManager.newTransaction {
            val order = orderRepository.findById(context.orderId) ?: return@newTransaction null

            val routeLengthKm = routeService.calculateRouteLengthKm(order.route?.route.orEmpty())

            var extendDTO: OrderDetailExtendDTO? = null

            if (context.includes.isNotEmpty()) {
                var fetchedTrainSets: List<TrainSetDTO>? = null
                var fetchedCargoTypes: List<CargoTypeDTO>? = null
                var fetchedStations: List<StationDTO>? = null
                var fetchedWagons: List<WagonDTO>? = null
                var fetchedUsers: List<UserDTO>? = null
                var fetchedRailwayNodes: List<RailwayNodeDTO>? = null

                if (context.includes.contains("trainSet") && order.trainSetId != null) {
                    fetchedTrainSets = trainSetRepository.selectByIds(listOf(order.trainSetId)).map { it.toDTO() }
                }

                if (context.includes.contains("cargoType") && order.cargo != null) {
                    fetchedCargoTypes = cargoTypeRepository.selectByIds(listOf(order.cargo.cargoTypeId)).map { it.toDTO() }
                }

                if (context.includes.contains("wagon") && order.cargo != null) {
                    fetchedWagons = wagonRepository.selectByIds(listOf(order.cargo.wagonId)).map { it.toDTO() }
                }

                if (context.includes.contains("station") && order.route != null) {
                    val stationIds = listOf(Station.Id(order.route.stationFrom), Station.Id(order.route.stationTo))
                    fetchedStations = stationRepository.selectByIds(stationIds).map { it.toDTO() }
                }

                if (context.includes.contains("user")) {
                    fetchedUsers = userRepository.select(listOf(User.Id(order.userId))).map { it.toDTO() }
                }

                if (context.includes.contains("railwayNode") && order.route != null) {
                    val nodeIds = order.route.route.distinct()
                    fetchedRailwayNodes = railwayNodeRepository.selectByIds(nodeIds).map { it.toDTO() }
                }

                extendDTO = OrderDetailExtendDTO(
                    trainSets = fetchedTrainSets,
                    cargoTypes = fetchedCargoTypes,
                    stations = fetchedStations,
                    wagons = fetchedWagons,
                    users = fetchedUsers,
                    railwayNodes = fetchedRailwayNodes
                )
            }

            OrderDetailResponseDTO(
                order = order.toDTO(),
                routeLengthKm = routeLengthKm,
                extend = extendDTO
            )
        }
        return Result(response)
    }
}
