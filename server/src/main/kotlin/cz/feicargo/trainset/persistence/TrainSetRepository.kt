package cz.feicargo.trainset.persistence

import cz.feicargo.trainset.entity.TrainSet

interface TrainSetRepository {
    suspend fun search(page: Int, size: Int): Pair<List<TrainSet>, Long>
}
