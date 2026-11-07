package cz.feicargo.trainset.api.rest.dto

import cz.feicargo.trainset.entity.TrainSet
import kotlinx.serialization.Serializable

@Serializable
data class TrainSetDTO(
    val id: String,
    val code: String,
    val locomotiveId: Long,
    val departureDate: String,
    val arrivalDate: String,
    val expirationDate: String,
)

fun TrainSet.toDTO() = TrainSetDTO(
    id = this.id.value.toString(),
    code = this.code,
    locomotiveId = this.locomotiveId,
    departureDate = this.departureDate.toString(),
    arrivalDate = this.arrivalDate.toString(),
    expirationDate = this.expirationDate.toString(),
)
