package cz.feicargo.order.persistence.exposed

import cz.feicargo.order.entity.Order
import cz.feicargo.order.persistence.OrderRepository
import kotlinx.coroutines.flow.associateBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.uuid.Uuid

class OrderExposedRepository : OrderRepository {
    private fun selectQuery() = OrderTable.select(OrderTable.columns)

    override suspend fun search(
        page: Int,
        size: Int,
        userId: Uuid?,
    ): Pair<List<Order>, Long> {
        val query = selectQuery()

        if (userId != null) {
            query.andWhere { OrderTable.userId eq userId }
        }

        val totalCount = query.count()

        val orderRows =
            query
                .orderBy(OrderTable.loadDate to SortOrder.DESC)
                .limit(size)
                .offset((page * size).toLong())
                .toList()

        if (orderRows.isEmpty()) {
            return emptyList<Order>() to totalCount
        }

        val orderIds = orderRows.map { it[OrderTable.id].value }

        val routesByOrderId =
            OrderRouteTable
                .select(OrderRouteTable.columns)
                .where { OrderRouteTable.orderId inList orderIds }
                .map(ResultRow::toOrderRoute)
                .toList()
                .associateBy { it.orderId }

        val cargoesByOrderId =
            OrderCargoTable
                .select(OrderCargoTable.columns)
                .where { OrderCargoTable.orderId inList orderIds }
                .map(ResultRow::toOrderCargo)
                .toList()
                .associateBy { it.orderId }

        val orders =
            orderRows.map { row ->
                val id = row[OrderTable.id].value
                row.toOrder(
                    route = routesByOrderId[id],
                    cargo = cargoesByOrderId[id],
                )
            }

        return orders to totalCount
    }

    override suspend fun selectByTrainSetId(trainSetId: Uuid): List<Order> {
        val ordersRaw =
            selectQuery()
                .where { OrderTable.trainSetId eq trainSetId }
                .map { it }
                .toList()

        if (ordersRaw.isEmpty()) return emptyList()
        val orderIds = ordersRaw.map { it[OrderTable.id].value.let(Order::Id) }
        val uuidList = orderIds.map { it.value }

        val routes =
            OrderRouteTable
                .select(OrderRouteTable.columns)
                .where { OrderRouteTable.orderId inList uuidList }
                .map(ResultRow::toOrderRoute)
                .associateBy { it.orderId }

        val cargoes =
            OrderCargoTable
                .select(OrderCargoTable.columns)
                .where { OrderCargoTable.orderId inList uuidList }
                .map(ResultRow::toOrderCargo)
                .associateBy { it.orderId }

        return ordersRaw.map { row ->
            val oId = row[OrderTable.id].value
            row.toOrder(routes[oId], cargoes[oId])
        }
    }

    override suspend fun findById(id: Uuid): Order? {
        val row = selectQuery()
            .where { OrderTable.id eq id }
            .map { it }
            .toList()
            .firstOrNull() ?: return null

        val route = OrderRouteTable.select(OrderRouteTable.columns)
            .where { OrderRouteTable.orderId eq id }
            .map(ResultRow::toOrderRoute)
            .toList()
            .firstOrNull()

        val cargo = OrderCargoTable.select(OrderCargoTable.columns)
            .where { OrderCargoTable.orderId eq id }
            .map(ResultRow::toOrderCargo)
            .toList()
            .firstOrNull()

        return row.toOrder(route, cargo)
    }
}
