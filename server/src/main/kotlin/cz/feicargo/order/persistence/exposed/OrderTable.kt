package cz.feicargo.order.persistence.exposed

import cz.feicargo.order.entity.Order
import cz.feicargo.order.entity.OrderCargo
import cz.feicargo.order.entity.OrderRoute
import cz.feicargo.order.entity.OrderStatusHistory
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.javatime.timestamp
import java.util.UUID
import kotlin.uuid.toKotlinUuid

object OrderTable : UuidTable("orders") {
    val userId = uuid("user_id")
    val trainSetId = uuid("train_set_id").nullable()
    val name = text("name")
    val status = text("status")
    val code = text("code")
    val totalPrice = decimal("total_price", 12, 2)
    val locomotivePrice = decimal("locomotive_price", 12, 2)
    val loadDate = timestamp("load_date").nullable()
    val unloadDate = timestamp("unload_date").nullable()
    val preferredDeparture = timestamp("preferred_departure")
    val preferredArrival = timestamp("preferred_arrival")
}

object OrderRouteTable : UuidTable("order_routes") {
    val orderId = uuid("order_id")
    val stationFrom = uuid("station_from")
    val stationTo = uuid("station_to")
    val route = array<UUID>("route")
}

object OrderCargoTable : UuidTable("order_cargoes") {
    val orderId = uuid("order_id")
    val cargoTypeId = long("cargo_type_id")
    val volume = double("volume")
    val weight = double("weight")
    val wagonId = long("wagon_id")
    val wagonCount = integer("wagon_count")
}

object OrderStatusHistoryTable : UuidTable("order_status_history") {
    val orderId = uuid("order_id")
    val status = text("status")
    val changedAt = timestamp("changed_at")
}

fun ResultRow.toOrderStatusHistory() = OrderStatusHistory(
    id = this[OrderStatusHistoryTable.id].value.let(OrderStatusHistory::Id),
    orderId = this[OrderStatusHistoryTable.orderId],
    status = this[OrderStatusHistoryTable.status],
    changedAt = this[OrderStatusHistoryTable.changedAt],
)

fun ResultRow.toOrderRoute() =
    OrderRoute(
        id = this[OrderRouteTable.id].value.let(OrderRoute::Id),
        orderId = this[OrderRouteTable.orderId],
        stationFrom = this[OrderRouteTable.stationFrom],
        stationTo = this[OrderRouteTable.stationTo],
        route = this[OrderRouteTable.route].map { it.toKotlinUuid() },
    )

fun ResultRow.toOrderCargo() =
    OrderCargo(
        id = this[OrderCargoTable.id].value.let(OrderCargo::Id),
        orderId = this[OrderCargoTable.orderId],
        cargoTypeId = this[OrderCargoTable.cargoTypeId],
        volume = this[OrderCargoTable.volume],
        weight = this[OrderCargoTable.weight],
        wagonId = this[OrderCargoTable.wagonId],
        wagonCount = this[OrderCargoTable.wagonCount],
    )

fun ResultRow.toOrder(
    route: OrderRoute?,
    cargo: OrderCargo?,
) = Order(
    id = this[OrderTable.id].value.let(Order::Id),
    userId = this[OrderTable.userId],
    trainSetId = this[OrderTable.trainSetId],
    name = this[OrderTable.name],
    status = this[OrderTable.status],
    code = this[OrderTable.code],
    totalPrice = this[OrderTable.totalPrice],
    locomotivePrice = this[OrderTable.locomotivePrice],
    loadDate = this[OrderTable.loadDate],
    unloadDate = this[OrderTable.unloadDate],
    preferredDeparture = this[OrderTable.preferredDeparture],
    preferredArrival = this[OrderTable.preferredArrival],
    route = route,
    cargo = cargo,
)
