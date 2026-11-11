package cz.feicargo.user.persistence.exposed

import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.uuid.Uuid

class UserExposedRepository : UserRepository {
    private fun selectQuery() = UserTable.select(UserTable.columns)

    override suspend fun selectByIds(ids: List<Uuid>): List<User> {
        if (ids.isEmpty()) return emptyList()
        return selectQuery()
            .where { UserTable.id inList ids }
            .map(ResultRow::toUser)
            .toList()
    }
}
