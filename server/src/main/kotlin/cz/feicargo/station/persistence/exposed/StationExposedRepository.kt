package cz.feicargo.station.persistence.exposed

import cz.feicargo.commons.persistence.exposed.toPrefixTsQuery
import cz.feicargo.commons.persistence.exposed.toTsQuery
import cz.feicargo.commons.persistence.exposed.tsMatch
import cz.feicargo.commons.persistence.exposed.tsRank
import cz.feicargo.station.entity.Station
import cz.feicargo.station.persistence.StationRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.select

class StationExposedRepository : StationRepository {
    // Query je v Exposed mutabilní (where/orderBy mění instanci), proto se pro každé volání vytváří nová
    private fun selectQuery() =
        StationTable.select(
            StationTable.id,
            StationTable.name,
        )

    override suspend fun select(): List<Station> =
        selectQuery()
            .orderBy(StationTable.name, SortOrder.ASC)
            .map(ResultRow::toStation)
            .toList()

    override suspend fun <I> select(value: I): List<Station> =
        when (value) {
            is Station.Id -> selectById(value)
            is String -> selectByFulltext(value)
            else -> throw NotImplementedError()
        }

    private suspend fun selectById(id: Station.Id): List<Station> =
        selectQuery()
            .where { StationTable.id eq id.let(Station.Id::value) }
            .map(ResultRow::toStation)
            .toList()

    private suspend fun selectByFulltext(value: String): List<Station> {
        val tsQuery = value.toPrefixTsQuery().ifEmpty { return emptyList() }
        val query = toTsQuery(tsQuery)
        return selectQuery()
            .where { StationTable.searchVector tsMatch query }
            .orderBy(
                tsRank(StationTable.searchVector, query) to SortOrder.DESC,
                StationTable.name to SortOrder.ASC,
            ).map(ResultRow::toStation)
            .toList()
    }
}
