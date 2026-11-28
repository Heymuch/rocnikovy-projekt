package cz.feicargo.user.api.rest

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/users")
class Users {
    @Serializable
    @Resource("/search")
    class Search(
        val parent: Users = Users(),
        val page: Int = 0,
        val size: Int = 20,
    )

    @Serializable
    @Resource("/{id}/block")
    class Block(
        val parent: Users = Users(),
        val id: String,
    )
}
