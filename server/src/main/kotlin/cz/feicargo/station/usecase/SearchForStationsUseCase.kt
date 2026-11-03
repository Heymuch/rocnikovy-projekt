package cz.feicargo.station.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository

class SearchForStationsUseCase(
    private val stationRepository: StationRepository,
    private val transactionManager: TransactionManager,
) : UseCase<SearchForStationsUseCase.Context, SearchForStationsUseCase.Result> {
    data class Context(
        val fulltextSearchValue: String,
    ) : UseCase.Context

    data class Result(
        val stations: List<Station>,
    ) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val stations =
            transactionManager.newTransaction {
                stationRepository.select(context.fulltextSearchValue)
            }
        return Result(stations)
    }
}
