package cz.feicargo.user.persistence

import cz.feicargo.user.entity.User
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun select(ids: List<User.Id>): List<User>
    suspend fun select(email: User.Email): User?
    suspend fun search(page: Int, size: Int, excludeUserId: Uuid): Pair<List<User>, Long>
    suspend fun updateBlocked(id: User.Id, isBlocked: Boolean): User?
}
