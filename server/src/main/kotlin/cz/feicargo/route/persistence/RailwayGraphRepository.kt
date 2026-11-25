package cz.feicargo.route.persistence

import cz.feicargo.route.entity.RailwayEdge
import kotlin.uuid.Uuid

interface RailwayGraphRepository {
    suspend fun selectEdgesBetweenNodes(nodeIds: List<Uuid>): List<RailwayEdge>
}
