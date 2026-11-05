package cz.feicargo.cargotype.api.rest.dto.response

import cz.feicargo.cargotype.api.rest.dto.CargoTypeDTO
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class CargoTypeResponseDTO(
    val items: List<CargoTypeDTO>,
    val timestamp: Instant = Clock.System.now()
)
