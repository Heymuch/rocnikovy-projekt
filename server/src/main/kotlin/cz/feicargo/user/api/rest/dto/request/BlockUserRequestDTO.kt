package cz.feicargo.user.api.rest.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class BlockUserRequestDTO(
    val isBlocked: Boolean,
)

@Serializable
data class BlockUserResponseDTO(
    val id: String,
    val isBlocked: Boolean,
)
