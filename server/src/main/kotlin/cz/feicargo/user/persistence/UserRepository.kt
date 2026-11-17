package cz.feicargo.user.persistence

import cz.feicargo.user.entity.User

interface UserRepository {
    suspend fun select(ids: List<User.Id>): List<User>
    suspend fun select(email: User.Email): User?
}
