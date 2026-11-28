package cz.feicargo.wagon.api.rest.dto

import cz.feicargo.wagon.entity.Wagon
import kotlinx.serialization.Serializable

@Serializable
data class WagonDTO(
    val id: Long,
    val name: String,
    val fixedRentPrice: Double,
)

fun Wagon.toDTO() =
    WagonDTO(
        id = this.id.value,
        name = this.name,
        fixedRentPrice = this.fixedRentPrice,
    )
