package cz.feicargo.route.persistence

import cz.feicargo.route.entity.RailwayNode
import kotlin.uuid.Uuid

interface RailwayNodeRepository {
    suspend fun selectByIds(ids: List<Uuid>): List<RailwayNode>
}
