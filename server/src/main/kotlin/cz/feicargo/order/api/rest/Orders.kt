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
        val userId: String? = null,
    )
    @Serializable
    @Resource("/{id}")
    class Detail(
        val parent: Orders = Orders(),
        val id: String
    )
    @Serializable
    @Resource("/calculate")
    class Calculate(
        val parent: Orders = Orders(),
    )
    @Serializable
    @Resource("/{id}")
    class Update(
        val parent: Orders = Orders(),
        val id: String,
    )
}


