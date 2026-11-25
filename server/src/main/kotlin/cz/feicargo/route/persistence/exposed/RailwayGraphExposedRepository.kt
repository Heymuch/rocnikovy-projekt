package cz.feicargo.route.persistence.exposed

import cz.feicargo.route.entity.RailwayEdge
import cz.feicargo.route.persistence.RailwayGraphRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.uuid.Uuid

class RailwayGraphExposedRepository : RailwayGraphRepository {
    private fun selectQuery() = RailwayGraphTable.select(RailwayGraphTable.columns)

    override suspend fun selectEdgesBetweenNodes(nodeIds: List<Uuid>): List<RailwayEdge> {
        if (nodeIds.size < 2) return emptyList()
        return selectQuery()
            .where { (RailwayGraphTable.nodeAId inList nodeIds) and (RailwayGraphTable.nodeBId inList nodeIds) }
            .map(ResultRow::toRailwayEdge)
            .toList()
    }
}
