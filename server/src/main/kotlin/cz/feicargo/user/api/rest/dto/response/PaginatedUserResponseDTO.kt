package cz.feicargo.user.api.rest.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserOverviewDTO(
    val id: String,
    val name: String,
    val email: String,
    val isBlocked: Boolean,
    val ordersCount: Int,
    val totalSpent: Double,
    val lastActivityAt: String?,
)

@Serializable
data class PaginatedUserResponseDTO(
    val items: List<UserOverviewDTO>,
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val totalPages: Int,
)
