package cz.feicargo.trainset.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.trainset.entity.TrainSet
import cz.feicargo.trainset.persistence.TrainSetRepository

class SearchTrainSetsUseCase(
    private val repository: TrainSetRepository,
    private val transactionManager: TransactionManager,
) : UseCase<SearchTrainSetsUseCase.Context, SearchTrainSetsUseCase.Result> {
    data class Context(val page: Int, val size: Int) : UseCase.Context
    data class Result(val trainSets: List<TrainSet>, val totalCount: Long) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val (trains, total) = transactionManager.newTransaction {
            repository.search(context.page, context.size)
        }
        return Result(trains, total)
    }
}
