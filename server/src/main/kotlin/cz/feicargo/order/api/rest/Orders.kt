package cz.feicargo.order.api.rest

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/orders")
class Orders {
    @Serializable
    @Resource("/search")
    class Search(
        val parent: Orders = Orders(),
        val page: Int,
        val size: Int,
        val userId: String? = null
    )
}
