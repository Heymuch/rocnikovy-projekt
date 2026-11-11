package cz.feicargo.locomotive.persistence.exposed

import cz.feicargo.locomotive.entity.Locomotive
import cz.feicargo.locomotive.persistence.LocomotiveRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select

class LocomotiveExposedRepository : LocomotiveRepository {
    private fun selectQuery() = LocomotiveTable.select(LocomotiveTable.columns)

    override suspend fun selectByIds(ids: List<Long>): List<Locomotive> {
        if (ids.isEmpty()) return emptyList()
        return selectQuery()
            .where { LocomotiveTable.id inList ids }
            .map(ResultRow::toLocomotive)
            .toList()
    }
}
