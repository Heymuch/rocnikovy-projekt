package cz.feicargo.route.persistence.exposed

import cz.feicargo.route.entity.RailwayNode
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable

object RailwayNodeTable : UuidTable("railway_nodes") {
    val latitude = double("latitude")
    val longitude = double("longitude")
}

fun ResultRow.toRailwayNode() = RailwayNode(
    id = this[RailwayNodeTable.id].value.let(RailwayNode::Id),
    latitude = this[RailwayNodeTable.latitude],
    longitude = this[RailwayNodeTable.longitude],
)
