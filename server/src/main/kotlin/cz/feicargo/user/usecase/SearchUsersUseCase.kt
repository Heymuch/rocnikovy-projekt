package cz.feicargo.user.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.user.api.rest.dto.response.UserOverviewDTO
import cz.feicargo.user.persistence.UserRepository
import kotlin.uuid.Uuid

class SearchUsersUseCase(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val transactionManager: TransactionManager,
) : UseCase<SearchUsersUseCase.Context, SearchUsersUseCase.Result> {

    data class Context(
        val page: Int,
        val size: Int,
        val currentUserId: Uuid,
    ) : UseCase.Context

    data class Result(
        val users: List<UserOverviewDTO>,
        val totalCount: Long,
    ) : UseCase.Result

    override suspend fun invoke(context: Context): Result {
        require(context.page >= 0) { "Page must be greater than or equal to 0." }
        require(context.size in 1..100) { "Size must be between 1 and 100." }

        return transactionManager.newTransaction {
            val (users, totalCount) = userRepository.search(
                page = context.page,
                size = context.size,
                excludeUserId = context.currentUserId,
            )

            val statsByUserId = orderRepository.selectUserStats(users.map { it.id.value })

            val overviewItems = users.map { user ->
                val stats = statsByUserId[user.id.value]
                UserOverviewDTO(
                    id = user.id.value.toString(),
                    name = user.name.value,
                    email = user.email.value,
                    isBlocked = user.isBlocked,
                    ordersCount = stats?.ordersCount ?: 0,
                    totalSpent = stats?.totalSpent?.toDouble() ?: 0.0,
                    lastActivityAt = stats?.lastActivityAt?.toString(),
                )
            }

            Result(overviewItems, totalCount)
        }
    }
}
