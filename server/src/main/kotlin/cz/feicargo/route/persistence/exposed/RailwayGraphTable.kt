package cz.feicargo.route.persistence.exposed

import cz.feicargo.route.entity.RailwayEdge
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

object RailwayGraphTable : Table("railway_graph") {
    val nodeAId = uuid("node_a_id")
    val nodeBId = uuid("node_b_id")
    val distance = integer("distance")

    override val primaryKey = PrimaryKey(nodeAId, nodeBId)
}

fun ResultRow.toRailwayEdge() = RailwayEdge(
    nodeAId = this[RailwayGraphTable.nodeAId],
    nodeBId = this[RailwayGraphTable.nodeBId],
    distanceMeters = this[RailwayGraphTable.distance],
)
