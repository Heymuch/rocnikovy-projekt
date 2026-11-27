package cz.feicargo.order.usecase

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.usecase.UseCase
import cz.feicargo.order.persistence.OrderRepository
import kotlin.uuid.Uuid

class DeleteOrderUseCase(
    private val orderRepository: OrderRepository,
    private val transactionManager: TransactionManager,
) : UseCase<DeleteOrderUseCase.Context, DeleteOrderUseCase.Result> {

    data class Context(
        val orderId: Uuid,
        val userId: Uuid,
        val isAdmin: Boolean,
    ) : UseCase.Context

    sealed interface Result : UseCase.Result {
        data object Success : Result
        data object NotFound : Result
        data object Forbidden : Result
        data class InvalidStatus(val currentStatus: String) : Result
    }

    override suspend fun invoke(context: Context): Result =
        transactionManager.newTransaction {
            val existingOrder = orderRepository.findById(context.orderId)
                ?: return@newTransaction Result.NotFound

            if (!context.isAdmin && existingOrder.userId != context.userId) {
                return@newTransaction Result.Forbidden
            }

            if (existingOrder.status != "IN_QUEUE") {
                return@newTransaction Result.InvalidStatus(existingOrder.status)
            }

            orderRepository.delete(context.orderId)
            Result.Success
        }
}
