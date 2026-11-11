package cz.feicargo.trainset.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.locomotive.api.rest.dto.LocomotiveDTO
import cz.feicargo.locomotive.api.rest.dto.toDTO
import cz.feicargo.locomotive.persistence.LocomotiveRepository
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.trainset.api.rest.dto.response.TrainSetDetailExtendDTO
import cz.feicargo.trainset.api.rest.dto.response.TrainSetDetailResponseDTO
import cz.feicargo.trainset.api.rest.dto.response.TrainSetOrderDetailDTO
import cz.feicargo.trainset.persistence.TrainSetRepository
import cz.feicargo.user.api.rest.dto.toDTO
import cz.feicargo.user.persistence.UserRepository
import cz.feicargo.wagon.api.rest.dto.WagonDTO
import cz.feicargo.wagon.api.rest.dto.toDTO
import cz.feicargo.wagon.persistence.WagonRepository
import kotlin.uuid.Uuid

class GetTrainSetDetailUseCase(
    private val trainSetRepository: TrainSetRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val stationRepository: StationRepository,
    private val locomotiveRepository: LocomotiveRepository,
    private val wagonRepository: WagonRepository,
    private val transactionManager: TransactionManager,
) : UseCase<GetTrainSetDetailUseCase.Context, GetTrainSetDetailUseCase.Result> {

    data class Context(val trainSetId: Uuid, val includes: List<String>) : UseCase.Context
    data class Result(val detail: TrainSetDetailResponseDTO?) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val response = transactionManager.newTransaction {
            val trainSet = trainSetRepository.findById(context.trainSetId) ?: return@newTransaction null
            val orders = orderRepository.selectByTrainSetId(context.trainSetId)

            val userIds = orders.map { it.userId }.distinct()
            val users = userRepository.selectByIds(userIds).associateBy { it.id.value }

            val totalWagons = orders.flatMap { it.cargoes }.sumOf { it.wagonCount }
            val totalPrice = orders.sumOf { it.totalPrice.toDouble() }

            val earliestOrder = orders.minByOrNull { it.preferredDeparture }
            val latestOrder = orders.maxByOrNull { it.preferredArrival }
            val stationFromId = earliestOrder?.route?.stationFrom
            val stationToId = latestOrder?.route?.stationTo

            val orderDetails = orders.map { order ->
                TrainSetOrderDetailDTO(
                    order = order.toDTO(),
                    user = users[order.userId]?.toDTO() ?: throw IllegalStateException("User not found")
                )
            }

            var fetchedLocomotives: List<LocomotiveDTO>? = null
            var fetchedWagons: List<WagonDTO>? = null
            var fetchedStations: List<StationDTO>? = null

            if (context.includes.isNotEmpty()) {
                if (context.includes.contains("locomotive")) {
                    fetchedLocomotives = locomotiveRepository.selectByIds(listOf(trainSet.locomotiveId)).map { it.toDTO() }
                }

                if (context.includes.contains("wagon")) {
                    val wagonIds = orders.flatMap { it.cargoes }.map { it.wagonId }.distinct()
                    fetchedWagons = wagonRepository.selectByIds(wagonIds).map { it.toDTO() }
                }

                if (context.includes.contains("station")) {
                    val stationIdsRaw = listOfNotNull(stationFromId, stationToId) + orders.mapNotNull { it.route }.flatMap { listOf(it.stationFrom, it.stationTo) }
                    fetchedStations = stationRepository.selectByIds(stationIdsRaw.distinct().map { Station.Id(it) }).map { it.toDTO() }
                }
            }

            val extendDTO = if (context.includes.isNotEmpty()) {
                TrainSetDetailExtendDTO(
                    stations = fetchedStations,
                    locomotives = fetchedLocomotives,
                    wagons = fetchedWagons
                )
            } else null

            TrainSetDetailResponseDTO(
                id = trainSet.id.value.toString(),
                code = trainSet.code,
                stationFromId = stationFromId?.toString(),
                stationToId = stationToId?.toString(),
                departureDate = trainSet.departureDate.toString(),
                arrivalDate = trainSet.arrivalDate.toString(),
                expirationDate = trainSet.expirationDate.toString(),
                totalWagons = totalWagons,
                totalPrice = totalPrice,
                routeLengthKm = 0.0,
                route = emptyList(),
                orders = orderDetails,
                extend = extendDTO
            )
        }
        return Result(response)
    }
}
