package cz.feicargo.station.persistence.exposed

import cz.feicargo.commons.persistence.exposed.tsVector
import cz.feicargo.route.persistence.exposed.RailwayNodeTable
import cz.feicargo.station.entity.Station
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import kotlin.uuid.Uuid

object StationTable : IdTable<Uuid>("stations") {
    override val id = reference("id", RailwayNodeTable.id)
    val name = text("name")
    val searchVector = tsVector("search_vector")
}

fun ResultRow.toStation() =
    Station(
        id = this[StationTable.id].value.let(Station::Id),
        name = this[StationTable.name].let(Station::Name),
    )
