package cz.feicargo.commons.persistence

import cz.feicargo.commons.entity.Entity

interface Repository {
    interface Read<T : Entity> {
        suspend fun select(): List<T>

        suspend fun <I> select(value: I): List<T>
    }
}
