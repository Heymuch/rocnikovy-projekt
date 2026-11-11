package cz.feicargo.trainset.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.station.api.rest.dto.StationDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.trainset.api.rest.dto.response.TrainSetExtendDTO
import cz.feicargo.trainset.entity.TrainSet
import cz.feicargo.trainset.persistence.TrainSetRepository

class SearchTrainSetsUseCase(
    private val repository: TrainSetRepository,
    private val stationRepository: StationRepository,
    private val transactionManager: TransactionManager,
) : UseCase<SearchTrainSetsUseCase.Context, SearchTrainSetsUseCase.Result> {

    data class Context(val page: Int, val size: Int, val includes: List<String>) : UseCase.Context
    data class Result(val trainSets: List<TrainSet>, val totalCount: Long, val extend: TrainSetExtendDTO?) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val (trainSets, total) = transactionManager.newTransaction {
            repository.search(context.page, context.size)
        }

        var extendDTO: TrainSetExtendDTO? = null
        if (trainSets.isNotEmpty() && context.includes.isNotEmpty()) {
            transactionManager.newTransaction {
                var fetchedStations: List<StationDTO>? = null

                if (context.includes.contains("station")) {
                    val stationIdsRaw = trainSets.flatMap { listOf(it.stationFrom, it.stationTo) }.distinct()
                    val stationIds = stationIdsRaw.map { Station.Id(it) }
                    fetchedStations = stationRepository.selectByIds(stationIds).map { it.toDTO() }
                }

                extendDTO = TrainSetExtendDTO(stations = fetchedStations)
            }
        }

        return Result(trainSets, total, extendDTO)
    }
}
