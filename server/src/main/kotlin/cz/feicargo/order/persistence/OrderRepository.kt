package cz.feicargo.order.persistence

import cz.feicargo.order.entity.Order
import kotlin.uuid.Uuid

interface OrderRepository {
    suspend fun search(page: Int, size: Int, userId: Uuid?): Pair<List<Order>, Long>
}
