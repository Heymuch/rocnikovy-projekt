package cz.feicargo.user.persistence.exposed

import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.uuid.Uuid

class UserExposedRepository : UserRepository {
    private fun selectQuery() = UserTable.select(UserTable.columns)

    override suspend fun select(ids: List<User.Id>): List<User> {
        if (ids.isEmpty()) return emptyList()
        return selectQuery()
            .where { UserTable.id inList ids.map(User.Id::value) }
            .map(ResultRow::toUser)
            .toList()
    }

    override suspend fun select(email: User.Email): User? {
        return selectQuery().where { UserTable.email eq email.let(User.Email::value) }
            .map(ResultRow::toUser)
            .singleOrNull()
    }
}
