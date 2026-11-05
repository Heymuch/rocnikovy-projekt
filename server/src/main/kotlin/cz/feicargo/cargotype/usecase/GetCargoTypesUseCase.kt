package cz.feicargo.cargotype.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.cargotype.entity.CargoType
import cz.feicargo.cargotype.persistence.CargoTypeRepository

class GetCargoTypesUseCase(
    private val repository: CargoTypeRepository,
    private val transactionManager: TransactionManager,
) : UseCase<GetCargoTypesUseCase.Context, GetCargoTypesUseCase.Result> {

    class Context : UseCase.Context
    data class Result(val cargoTypes: List<CargoType>) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        val types = transactionManager.newTransaction {
            repository.selectAll()
        }
        return Result(types)
    }
}
