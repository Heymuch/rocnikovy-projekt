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
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.r2dbc.update
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

    override suspend fun search(page: Int, size: Int, excludeUserId: Uuid): Pair<List<User>, Long> {
        val query = selectQuery().where { UserTable.id neq excludeUserId }
        val totalCount = query.count()

        val items = query
            .orderBy(UserTable.name to SortOrder.ASC)
            .limit(size)
            .offset((page * size).toLong())
            .map(ResultRow::toUser)
            .toList()

        return items to totalCount
    }

    override suspend fun updateBlocked(id: User.Id, isBlocked: Boolean): User? {
        val updatedRows = UserTable.update({ UserTable.id eq id.value }) {
            it[UserTable.isBlocked] = isBlocked
        }
        if (updatedRows == 0) return null

        return selectQuery()
            .where { UserTable.id eq id.value }
            .map(ResultRow::toUser)
            .firstOrNull()
    }
}
