package cz.feicargo.order.api.rest.dto

import cz.feicargo.order.entity.Order
import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val id: String,
    val userId: String,
    val trainSetId: String?,
    val status: String,
    val code: String,
    val totalPrice: Double,
    val loadDate: String,
    val unloadDate: String,
    val preferredDeparture: String,
    val preferredArrival: String,
    val route: OrderRouteDTO?,
    val cargoes: List<OrderCargoDTO>
)

fun Order.toDTO() = OrderDTO(
    id = this.id.value.toString(),
    userId = this.userId.toString(),
    trainSetId = this.trainSetId?.toString(),
    status = this.status,
    code = this.code,
    totalPrice = this.totalPrice.toDouble(),
    loadDate = this.loadDate.toString(),
    unloadDate = this.unloadDate.toString(),
    preferredDeparture = this.preferredDeparture.toString(),
    preferredArrival = this.preferredArrival.toString(),
    route = this.route?.toDTO(),
    cargoes = this.cargoes.map { it.toDTO() }
)
