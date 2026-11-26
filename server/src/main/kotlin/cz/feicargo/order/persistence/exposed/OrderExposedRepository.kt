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
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

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
                .orderBy(OrderTable.preferredDeparture to SortOrder.DESC)
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

    override suspend fun create(order: Order): Order {
        val orderId = OrderTable.insertAndGetId {
            it[id] = order.id.value
            it[userId] = order.userId
            it[trainSetId] = order.trainSetId
            it[name] = order.name
            it[status] = order.status
            it[code] = order.code
            it[totalPrice] = order.totalPrice
            it[loadDate] = order.loadDate
            it[unloadDate] = order.unloadDate
            it[preferredDeparture] = order.preferredDeparture
            it[preferredArrival] = order.preferredArrival
        }.value

        val route = order.route ?: throw IllegalStateException("Route is required for a new order")
        OrderRouteTable.insert {
            it[id] = route.id.value
            it[OrderRouteTable.orderId] = orderId
            it[stationFrom] = route.stationFrom
            it[stationTo] = route.stationTo
            it[OrderRouteTable.route] = route.route.map { node -> node.toJavaUuid() }
        }

        val cargo = order.cargo ?: throw IllegalStateException("Cargo is required for a new order")
        OrderCargoTable.insert {
            it[id] = cargo.id.value
            it[OrderCargoTable.orderId] = orderId
            it[cargoTypeId] = cargo.cargoTypeId
            it[volume] = cargo.volume
            it[weight] = cargo.weight
            it[wagonId] = cargo.wagonId
            it[wagonCount] = cargo.wagonCount
        }

        OrderStatusHistoryTable.insert {
            it[id] = Uuid.random()
            it[OrderStatusHistoryTable.orderId] = orderId
            it[status] = order.status
        }

        return order
    }

    override suspend fun update(order: Order): Order {
        val orderId = order.id.value

        OrderTable.update({ OrderTable.id eq orderId }) {
            it[name] = order.name
            it[totalPrice] = order.totalPrice
            it[preferredDeparture] = order.preferredDeparture
            it[preferredArrival] = order.preferredArrival
        }

        val route = order.route ?: throw IllegalStateException("Route is required for updating an order.")
        OrderRouteTable.update({ OrderRouteTable.orderId eq orderId }) {
            it[stationFrom] = route.stationFrom
            it[stationTo] = route.stationTo
            it[OrderRouteTable.route] = route.route.map { node -> node.toJavaUuid() }
        }

        val cargo = order.cargo ?: throw IllegalStateException("Cargo is required for updating an order.")
        OrderCargoTable.update({ OrderCargoTable.orderId eq orderId }) {
            it[cargoTypeId] = cargo.cargoTypeId
            it[volume] = cargo.volume
            it[weight] = cargo.weight
            it[wagonId] = cargo.wagonId
            it[wagonCount] = cargo.wagonCount
        }

        return order
    }

    override suspend fun delete(id: Uuid): Boolean {
        val deletedCount = OrderTable.deleteWhere { OrderTable.id eq id }
        return deletedCount > 0
    }
}
