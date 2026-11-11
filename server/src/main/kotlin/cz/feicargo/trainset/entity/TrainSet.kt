package cz.feicargo.trainset.entity

import cz.feicargo.commons.entity.Entity
import java.time.Instant
import kotlin.uuid.Uuid

data class TrainSet(
    val id: Id,
    val code: String,
    val locomotiveId: Long,
    val stationFrom: Uuid,
    val stationTo: Uuid,
    val departureDate: Instant,
    val arrivalDate: Instant,
    val expirationDate: Instant,
) : Entity {
    @JvmInline value class Id(val value: Uuid)
}
