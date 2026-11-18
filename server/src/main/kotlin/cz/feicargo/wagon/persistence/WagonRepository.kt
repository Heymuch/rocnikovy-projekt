package cz.feicargo.wagon.persistence

import cz.feicargo.wagon.entity.Wagon

interface WagonRepository {
    suspend fun selectByIds(ids: List<Long>): List<Wagon>
    suspend fun findByName(name: String): Wagon?
}
