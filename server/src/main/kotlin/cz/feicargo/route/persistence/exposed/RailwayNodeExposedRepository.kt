package cz.feicargo.route.persistence.exposed

import cz.feicargo.route.entity.RailwayNode
import cz.feicargo.route.persistence.RailwayNodeRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.uuid.Uuid

class RailwayNodeExposedRepository : RailwayNodeRepository {
    private fun selectQuery() = RailwayNodeTable.select(RailwayNodeTable.columns)

    override suspend fun selectByIds(ids: List<Uuid>): List<RailwayNode> {
        if (ids.isEmpty()) return emptyList()
        return selectQuery()
            .where { RailwayNodeTable.id inList ids }
            .map(ResultRow::toRailwayNode)
            .toList()
    }
}
