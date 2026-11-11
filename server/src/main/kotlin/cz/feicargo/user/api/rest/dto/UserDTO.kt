package cz.feicargo.user.api.rest.dto

import cz.feicargo.user.entity.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String,
    val name: String,
    val email: String,
)

fun User.toDTO() = UserDTO(
    id = this.id.value.toString(),
    name = this.name,
    email = this.email,
)
