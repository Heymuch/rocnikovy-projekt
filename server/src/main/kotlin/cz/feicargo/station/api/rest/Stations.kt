package cz.feicargo.station.api.rest

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Resource("/stations")
class Stations {
    @Serializable
    @Resource("/search")
    class Search(
        val parent: Stations = Stations(),
        val query: String? = null,
        val page: Int = 0,
        val size: Int = 20,
    )

    @Serializable
    @Resource("/{id}")
    class Get(
        val parent: Stations = Stations(),
        val id: Uuid,
    )
}
