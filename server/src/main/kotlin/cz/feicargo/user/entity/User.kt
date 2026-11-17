package cz.feicargo.user.entity

import cz.feicargo.commons.entity.Entity
import kotlin.uuid.Uuid

data class User(
    val id: Id,
    val email: Email,
    val name: Name,
    val isBlocked: Boolean,
) : Entity {
    @JvmInline
    value class Id(
        val value: Uuid,
    )
    
    @JvmInline
    value class Email(
        val value: String
    )
    
    @JvmInline
    value class Name(
        val value: String
    )
}
