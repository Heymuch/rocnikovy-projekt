package cz.feicargo.trainset.persistence.exposed

import cz.feicargo.trainset.entity.TrainSet
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.javatime.timestamp

object TrainSetTable : UuidTable("train_sets") {
    val locomotiveId = long("locomotive_id")
    val stationFrom = uuid("station_from")
    val stationTo = uuid("station_to")
    val code = text("code").uniqueIndex()

    val departureDate = timestamp("departure_date")
    val arrivalDate = timestamp("arrival_date")
    val expirationDate = timestamp("expiration_date")
}

fun ResultRow.toTrainSet() = TrainSet(
    id = this[TrainSetTable.id].value.let(TrainSet::Id),
    code = this[TrainSetTable.code],
    locomotiveId = this[TrainSetTable.locomotiveId],
    stationFrom = this[TrainSetTable.stationFrom],
    stationTo = this[TrainSetTable.stationTo],
    departureDate = this[TrainSetTable.departureDate],
    arrivalDate = this[TrainSetTable.arrivalDate],
    expirationDate = this[TrainSetTable.expirationDate]
)
