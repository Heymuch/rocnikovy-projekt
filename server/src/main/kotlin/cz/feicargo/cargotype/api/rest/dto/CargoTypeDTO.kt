package cz.feicargo.cargotype.api.rest.dto

import cz.feicargo.cargotype.entity.CargoType
import kotlinx.serialization.Serializable

@Serializable
data class CargoTypeDTO(
    val id: Long,
    val name: String,
)

fun CargoType.toDTO() = CargoTypeDTO(
    id = this.id.value,
    name = this.name.value,
)
