package cz.feicargo.user.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class User(
    val id: Id,
    val email: String,
    val name: String,
    val isBlocked: Boolean,
) : Entity {
    @JvmInline value class Id(val value: Uuid)
}
