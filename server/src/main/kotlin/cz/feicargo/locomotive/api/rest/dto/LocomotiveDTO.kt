package cz.feicargo.locomotive.api.rest.dto

import cz.feicargo.locomotive.entity.Locomotive
import kotlinx.serialization.Serializable


@Serializable
data class LocomotiveDTO(
    val id: Long, val name: String
)
fun Locomotive.toDTO() = LocomotiveDTO(
    this.id.value, this.name
)
