package cz.feicargo.trainset.api.rest

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/train-sets")
class TrainSets {
    @Serializable
    @Resource("/search")
    class Search(
        val parent: TrainSets = TrainSets(),
        val page: Int,
        val size: Int
    )
}
