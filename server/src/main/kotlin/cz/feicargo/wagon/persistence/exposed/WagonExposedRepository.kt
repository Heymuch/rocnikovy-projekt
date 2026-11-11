package cz.feicargo.wagon.persistence.exposed

import cz.feicargo.wagon.entity.Wagon
import cz.feicargo.wagon.persistence.WagonRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select

class WagonExposedRepository : WagonRepository {
    private fun selectQuery() = WagonTable.select(WagonTable.columns)

    override suspend fun selectByIds(ids: List<Long>): List<Wagon> {
        if (ids.isEmpty()) return emptyList()
        return selectQuery()
            .where { WagonTable.id inList ids }
            .map(ResultRow::toWagon)
            .toList()
    }
}
