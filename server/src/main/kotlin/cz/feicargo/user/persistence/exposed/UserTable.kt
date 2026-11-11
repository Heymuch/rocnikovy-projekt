package cz.feicargo.user.persistence.exposed

import cz.feicargo.user.entity.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UuidTable

object UserTable : UuidTable("users") {
    val email = text("email")
    val name = text("name")
    val isBlocked = bool("is_blocked")
}

fun ResultRow.toUser() = User(
    id = this[UserTable.id].value.let(User::Id),
    email = this[UserTable.email],
    name = this[UserTable.name],
    isBlocked = this[UserTable.isBlocked],
)
