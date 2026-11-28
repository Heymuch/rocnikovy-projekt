package cz.feicargo.user.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import kotlin.uuid.Uuid

class BlockUserUseCase(
    private val userRepository: UserRepository,
    private val transactionManager: TransactionManager,
) : UseCase<BlockUserUseCase.Context, BlockUserUseCase.Result> {

    data class Context(
        val targetUserId: Uuid,
        val currentUserId: Uuid,
        val isBlocked: Boolean,
    ) : UseCase.Context

    sealed interface Result : UseCase.Result {
        data class Success(val user: User) : Result
        data object NotFound : Result
        data object SelfBlockNotAllowed : Result
    }

    override suspend fun invoke(context: Context): Result {
        if (context.targetUserId == context.currentUserId) {
            return Result.SelfBlockNotAllowed
        }

        val updatedUser = transactionManager.newTransaction {
            userRepository.updateBlocked(User.Id(context.targetUserId), context.isBlocked)
        } ?: return Result.NotFound

        return Result.Success(updatedUser)
    }
}
