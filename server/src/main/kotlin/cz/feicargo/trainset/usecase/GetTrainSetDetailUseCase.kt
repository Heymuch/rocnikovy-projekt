package cz.feicargo.trainset.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.locomotive.api.rest.dto.LocomotiveDTO
import cz.feicargo.locomotive.api.rest.dto.toDTO
import cz.feicargo.locomotive.persistence.LocomotiveRepository
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.route.api.rest.dto.RailwayNodeDTO
import cz.feicargo.route.api.rest.dto.toDTO
import cz.feicargo.route.persistence.RailwayGraphRepository
import cz.feicargo.route.persistence.RailwayNodeRepository
import cz.feicargo.route.service.RouteService
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.trainset.api.rest.dto.response.TrainSetDetailExtendDTO
import cz.feicargo.trainset.api.rest.dto.response.TrainSetDetailResponseDTO
import cz.feicargo.trainset.api.rest.dto.response.TrainSetOrderDetailDTO
import cz.feicargo.trainset.persistence.TrainSetRepository
import cz.feicargo.user.api.rest.dto.toDTO
import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import cz.feicargo.wagon.api.rest.dto.toDTO
import cz.feicargo.wagon.persistence.WagonRepository
import kotlin.math.round
import kotlin.uuid.Uuid

class GetTrainSetDetailUseCase(
    private val trainSetRepository: TrainSetRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val stationRepository: StationRepository,
    private val locomotiveRepository: LocomotiveRepository,
    private val wagonRepository: WagonRepository,
    private val routeService: RouteService,
    private val railwayNodeRepository: RailwayNodeRepository,
    private val railwayGraphRepository: RailwayGraphRepository,
    private val transactionManager: TransactionManager,
) : UseCase<GetTrainSetDetailUseCase.Context, GetTrainSetDetailUseCase.Result> {
    data class Context(
        val trainSetId: Uuid,
        val includes: List<String>,
    ) : UseCase.Context

    data class Result(
        val detail: TrainSetDetailResponseDTO?,
    ) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val trainSet = transactionManager.newTransaction {
            trainSetRepository.findById(context.trainSetId)
        } ?: return Result(null)

        val routeNodes = routeService.findRoute(trainSet.stationFrom, trainSet.stationTo)

        val response =
            transactionManager.newTransaction {
                val trainSet = trainSetRepository.findById(context.trainSetId) ?: return@newTransaction null
                val orders = orderRepository.selectByTrainSetId(context.trainSetId)

                val userIds = orders.map { User.Id(it.userId) }.distinct()
                val users = userRepository.select(userIds).associateBy { it.id.value }

                val totalWagons = orders.mapNotNull { it.cargo }.sumOf { it.wagonCount }
                val totalPrice = orders.sumOf { it.totalPrice.toDouble() }

                val stationFromId = trainSet.stationFrom
                val stationToId = trainSet.stationTo

                val routeLengthKm = if (routeNodes.size >= 2) {
                    val edges = railwayGraphRepository.selectEdgesBetweenNodes(routeNodes.distinct())
                    val distanceMap = buildMap {
                        edges.forEach { edge ->
                            put(edge.nodeAId to edge.nodeBId, edge.distanceMeters)
                            put(edge.nodeBId to edge.nodeAId, edge.distanceMeters)
                        }
                    }
                    val totalMeters = routeNodes.zipWithNext().sumOf { (from, to) ->
                        distanceMap[from to to] ?: 0
                    }
                    round(totalMeters / 10.0) / 100.0
                } else {
                    0.0
                }

                val orderDetails =
                    orders.map { order ->
                        TrainSetOrderDetailDTO(
                            order = order.toDTO(),
                            user = users[order.userId]?.toDTO() ?: throw IllegalStateException("User not found"),
                        )
                    }

                var fetchedLocomotives: List<LocomotiveDTO>? = null
                var fetchedWagons: List<WagonDTO>? = null
                var fetchedStations: List<StationDTO>? = null
                var fetchedRailwayNodes: List<RailwayNodeDTO>? = null

                if (context.includes.isNotEmpty()) {
                    if (context.includes.contains("locomotive")) {
                        fetchedLocomotives = locomotiveRepository.selectByIds(listOf(trainSet.locomotiveId)).map { it.toDTO() }
                    }

                    if (context.includes.contains("wagon")) {
                        val wagonIds = orders.mapNotNull { it.cargo }.map { it.wagonId }.distinct()
                        fetchedWagons = wagonRepository.selectByIds(wagonIds).map { it.toDTO() }
                    }

                    if (context.includes.contains("station")) {
                        val stationIdsRaw =
                            listOfNotNull(stationFromId, stationToId) +
                                orders.mapNotNull { it.route }.flatMap { listOf(it.stationFrom, it.stationTo) }
                        fetchedStations = stationRepository.selectByIds(stationIdsRaw.distinct().map { Station.Id(it) }).map { it.toDTO() }
                    }

                    if (context.includes.contains("railwayNode") && routeNodes.isNotEmpty()) {
                        fetchedRailwayNodes = railwayNodeRepository.selectByIds(routeNodes.distinct()).map { it.toDTO() }
                    }
                }

                val extendDTO =
                    if (context.includes.isNotEmpty()) {
                        TrainSetDetailExtendDTO(
                            stations = fetchedStations,
                            locomotives = fetchedLocomotives,
                            wagons = fetchedWagons,
                            railwayNodes = fetchedRailwayNodes,
                        )
                    } else {
                        null
                    }

                TrainSetDetailResponseDTO(
                    id = trainSet.id.value.toString(),
                    code = trainSet.code,
                    stationFromId = stationFromId.toString(),
                    stationToId = stationToId.toString(),
                    departureDate = trainSet.departureDate.toString(),
                    arrivalDate = trainSet.arrivalDate.toString(),
                    expirationDate = trainSet.expirationDate.toString(),
                    totalWagons = totalWagons,
                    totalPrice = totalPrice,
                    routeLengthKm = routeLengthKm,
                    route = routeNodes.map { it.toString() },
                    orders = orderDetails,
                    extend = extendDTO,
                )
            }
        return Result(response)
    }
}
