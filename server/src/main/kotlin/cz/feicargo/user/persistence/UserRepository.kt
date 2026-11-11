package cz.feicargo.user.persistence

import cz.feicargo.user.entity.User
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun selectByIds(ids: List<Uuid>): List<User>
}
