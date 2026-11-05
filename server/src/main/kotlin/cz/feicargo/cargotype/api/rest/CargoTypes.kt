package cz.feicargo.cargotype.api.rest

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/cargo-types")
class CargoTypes {
    @Serializable
    @Resource("/all")
    class GetAll(val parent: CargoTypes = CargoTypes())
}
