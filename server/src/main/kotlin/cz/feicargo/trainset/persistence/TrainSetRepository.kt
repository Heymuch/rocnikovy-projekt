package cz.feicargo.trainset.persistence

import cz.feicargo.trainset.entity.TrainSet
import kotlin.uuid.Uuid

interface TrainSetRepository {
    suspend fun search(page: Int, size: Int): Pair<List<TrainSet>, Long>
    suspend fun selectByIds(ids: List<Uuid>): List<TrainSet>
}
