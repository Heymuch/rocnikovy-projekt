package cz.feicargo.order.persistence

import cz.feicargo.order.entity.Order
import cz.feicargo.order.entity.OrderStatusHistory
import kotlin.uuid.Uuid

interface OrderRepository {
    suspend fun search(
        page: Int,
        size: Int,
        userId: Uuid?,
    ): Pair<List<Order>, Long>

    suspend fun selectByTrainSetId(trainSetId: Uuid): List<Order>
    suspend fun findById(id: Uuid): Order?
    suspend fun create(order: Order): Order
    suspend fun update(order: Order): Order
    suspend fun delete(id: Uuid): Boolean
    suspend fun selectStatusHistoryByOrderId(orderId: Uuid): List<OrderStatusHistory>
}
